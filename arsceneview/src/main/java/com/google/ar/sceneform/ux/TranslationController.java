/*
 * Copyright 2018 Google LLC All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.ux;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.PickHitResult;
import com.google.ar.sceneform.collision.CollisionSystem;
import com.google.ar.sceneform.math.MathHelper;
import com.google.ar.sceneform.math.Matrix;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import io.github.sceneview.ar.ArSceneView;
import io.github.sceneview.ar.arcore.ArFrame;
import io.github.sceneview.ar.node.ArNode;
import io.github.sceneview.node.Node;
import io.github.sceneview.node.NodeParent;

/**
 * Manipulates the position of a {@link BaseTransformableNode} using a {@link
 * DragGestureRecognizer}. If not selected, the {@link BaseTransformableNode} will become selected
 * when the {@link DragGesture} starts.
 */
public class TranslationController extends BaseTransformationController<DragGesture> {
    @Nullable
    private HitResult lastArHitResult;
    @Nullable
    private PickHitResult lastPickHitResult;

    private Boolean isHitNode = false;

    @Nullable
    private Vector3 desiredLocalPosition;
    @Nullable
    private Quaternion desiredLocalRotation;

    private final Vector3 initialForward = new Vector3();

    private EnumSet<Plane.Type> allowedPlaneTypes = EnumSet.allOf(Plane.Type.class);

    private static final float LERP_SPEED = 12.0f;
    private static final float POSITION_LENGTH_THRESHOLD = 0.01f;
    private static final float ROTATION_DOT_THRESHOLD = 0.99f;

    public TranslationController(
            BaseTransformableNode transformableNode, DragGestureRecognizer gestureRecognizer) {
        super(transformableNode, gestureRecognizer);
    }

    /**
     * Sets which types of ArCore Planes this TranslationController is allowed to translate on.
     */
    public void setAllowedPlaneTypes(EnumSet<Plane.Type> allowedPlaneTypes) {
        this.allowedPlaneTypes = allowedPlaneTypes;
    }

    /**
     * Gets a reference to the EnumSet that determines which types of ArCore Planes this
     * TranslationController is allowed to translate on.
     */
    public EnumSet<Plane.Type> getAllowedPlaneTypes() {
        return allowedPlaneTypes;
    }

    @Override
    public void onFrameUpdated(FrameTime frameTime, Node node) {
        updatePosition(frameTime);
//        updateRotation(frameTime);
    }

    @Override
    public boolean isTransforming() {
        // As long as the transformable node is still interpolating towards the final pose, this
        // controller is still transforming.
        return super.isTransforming() || desiredLocalRotation != null || desiredLocalPosition != null;
    }

    @Override
    public boolean canStartTransformation(DragGesture gesture) {
        Node targetNode = gesture.getTargetNode();
        Log.d("DADAYA", "canStartTransformation: " + getTransformableNode().toString() + " -> " + (targetNode != null ? targetNode.toString() : null));
        if (targetNode == null) {
            return false;
        }

        BaseTransformableNode transformableNode = getTransformableNode();
        if (targetNode != transformableNode /*&& !targetNode.isDescendantOf(transformableNode)*/) {
            return false;
        }

        if (!transformableNode.isSelected() && !transformableNode.select()) {
            return false;
        }

        Matrix nodeTransformMatrix = transformableNode.getTransformationMatrix();
        Vector3 nodePosition = new Vector3();
        nodeTransformMatrix.decomposeTranslation(nodePosition);
        Vector3 nodeScale = new Vector3();
        nodeTransformMatrix.decomposeScale(nodeScale);
        Quaternion nodeRotation = new Quaternion();
        nodeTransformMatrix.decomposeRotation(nodeScale, nodeRotation);

        Vector3 nodeBack = Quaternion.rotateVector(nodeRotation, Vector3.back());

//        Log.d("DADAYA", "startTransformation: " + nodePosition.toString());

        //todo drag already child nodes
        initialForward.set(Quaternion.rotateVector(nodeRotation, Vector3.forward()));

//        Vector3 initialForwardInWorld = Quaternion.rotateVector(nodeRotation, Vector3.forward());
//        Node parent = transformableNode.getParentNode();
//        if (parent != null) {
//            Matrix parentNodeTransformMatrix = parent.getTransformationMatrix();
//            Vector3 parentNodeScale = new Vector3();
//            parentNodeTransformMatrix.decomposeScale(parentNodeScale);
//            Quaternion parentNodeRotation = new Quaternion();
//            parentNodeTransformMatrix.decomposeRotation(parentNodeScale, parentNodeRotation);
//            initialForward.set(Quaternion.inverseRotateVector(parentNodeRotation, initialForwardInWorld));
//        } else {
//            initialForward.set(initialForwardInWorld);
//        }


        return true;
    }

    private final Matrix _transformationMatrixInverted = new Matrix();

    @Override
    public void onContinueTransformation(DragGesture gesture) {
        ArSceneView scene = (ArSceneView) getTransformableNode().getSceneViewInternal();
        if (scene == null) {
            return;
        }

        ArFrame frame = Objects.requireNonNull(scene.getSession()).getCurrentFrame();
        if (frame == null) {
            return;
        }
        CollisionSystem collisionSystem = scene.getCollisionSystem();

        Camera arCamera = frame.getCamera();
        if (arCamera.getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Vector3 position = gesture.getPosition();
        ArrayList<PickHitResult> hitNodesList = new ArrayList<>();
        collisionSystem.raycastAll(
                scene.getCamera().screenPointToRay(position.x, position.y),
                hitNodesList,
                (pickHitResult, collider) -> pickHitResult.setNode((Node) collider.getTransformProvider()),
                PickHitResult::new
        );

//        Log.d("DADAYA", "onContinueTransformation nodes: " + hitNodesList.size());
        if (checkHitNodes(hitNodesList)) {
            isHitNode = true;
            return;
        }

        List<HitResult> hitPlanesList = frame.hitTests(position.x, position.y);
//        Log.d("DADAYA", "onContinueTransformation planes: " + hitPlanesList.size());
        if (checkHitPlanes(hitPlanesList)) {
            isHitNode = false;
        }
    }

    private boolean checkHitPlanes(List<HitResult> hitPlanesList) {
        for (int i = 0; i < hitPlanesList.size(); i++) {
            HitResult hit = hitPlanesList.get(i);
            Trackable trackable = hit.getTrackable();
            Pose pose = hit.getHitPose();
            if (trackable instanceof Plane) {
                Plane plane = (Plane) trackable;
                if (plane.isPoseInPolygon(pose) && allowedPlaneTypes.contains(plane.getType())) {
                    desiredLocalPosition = new Vector3(pose.tx(), pose.ty(), pose.tz());
//                    Log.d("DADAYA", "plane: " + pose.toString());
//                    desiredLocalRotation = new Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw());
//                    Log.d("DADAYA", "continueBefore: " + desiredLocalPosition);
                    ArNode node = (ArNode) getTransformableNode();
                    Matrix.invert(node.getWorldTransformationMatrix(), _transformationMatrixInverted);
                    desiredLocalPosition = _transformationMatrixInverted.transformPoint(desiredLocalPosition);
                    Node parent = node.getParentNode();
                    if (parent != null && desiredLocalPosition != null) {
                        desiredLocalPosition = parent.getTransformationMatrixInverted().transformPoint(desiredLocalPosition);
                    }
//                    Node parent = getTransformableNode().getParentNode();
//                    if (parent != null && desiredLocalPosition != null && desiredLocalRotation != null) {
//                        Matrix parentNodeTransformMatrix = parent.getTransformationMatrix();
//                        Vector3 parentNodeScale = new Vector3();
//                        parentNodeTransformMatrix.decomposeScale(parentNodeScale);
//                        Quaternion parentNodeRotation = new Quaternion();
//                        parentNodeTransformMatrix.decomposeRotation(parentNodeScale, parentNodeRotation);
//
//                        desiredLocalPosition = parent.getTransformationMatrix().transformPoint(desiredLocalPosition);
//                        desiredLocalRotation =
//                                Quaternion.multiply(parentNodeRotation.inverted(),
//                                        Preconditions.checkNotNull(desiredLocalRotation));
//                    }
//                    Log.d("DADAYA", "continueAfter: " + desiredLocalPosition);


//                    desiredLocalPosition = Vector3.subtract(desiredLocalPosition, initialPosition);
//                    desiredLocalRotation =
//                            calculateFinalDesiredLocalRotation(Preconditions.checkNotNull(desiredLocalRotation));
                    lastArHitResult = hit;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkHitNodes(ArrayList<PickHitResult> hitNodesList) {
        for (int i = 0; i < hitNodesList.size(); i++) {
            PickHitResult hit = hitNodesList.get(i);
            Node trackable = hit.getNode();
            Vector3 pos = hit.getPoint();
            if (trackable instanceof ArNode) {
                ArNode node = (ArNode) getTransformableNode();
                if (trackable == node || trackable.isDescendantOf(node)) {
                    continue;
                }
//                Log.d("DADAYA", "node: " + pos.toString());
                desiredLocalPosition = pos;
                Matrix.invert(node.getWorldTransformationMatrix(), _transformationMatrixInverted);
                desiredLocalPosition = _transformationMatrixInverted.transformPoint(desiredLocalPosition);
                Node parent = node.getParentNode();
                if (parent != null && desiredLocalPosition != null) {
                    desiredLocalPosition = parent.getTransformationMatrixInverted().transformPoint(desiredLocalPosition);
                }
                lastPickHitResult = hit;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEndTransformation(DragGesture gesture) {
        Log.d("DADAYA", "onEndTransformation: " + isHitNode.toString());
        if (isHitNode) {
            PickHitResult pickHitResult = lastPickHitResult;
            if (pickHitResult == null || pickHitResult.getNode() == null) return;
            ArNode anchorNode = (ArNode) getTransformableNode();
            Matrix invertedMatrix = pickHitResult.getNode().getTransformationMatrixInverted();


            Vector3 localPos = invertedMatrix.transformPoint(pickHitResult.getPoint());
            anchorNode.setAnchor(null);
            pickHitResult.getNode().addChild(anchorNode);
            anchorNode.setWorldPosition(Vector3.zero(), false);
            anchorNode.setWorldRotation(new Quaternion(), false);
            anchorNode.setPosition(localPos);
            anchorNode.setRotationQuaternion(
                    Quaternion.rotationBetweenVectors(Vector3.forward(), invertedMatrix.transformDirection(initialForward))
            );

            desiredLocalPosition = localPos;
        } else {
            HitResult hitResult = lastArHitResult;
            if (hitResult == null) return;

            if (hitResult.getTrackable().getTrackingState() == TrackingState.TRACKING) {
                ArNode anchorNode = getAnchorNodeOrDie();

                Matrix nodeTransformMatrix = anchorNode.getTransformationMatrix();
                Vector3 nodePosition = new Vector3();
                nodeTransformMatrix.decomposeTranslation(nodePosition);
                Vector3 nodeScale = new Vector3();
                nodeTransformMatrix.decomposeScale(nodeScale);
                Quaternion nodeRotation = new Quaternion();
                nodeTransformMatrix.decomposeRotation(nodeScale, nodeRotation);

                anchorNode.setWorldPosition(nodePosition, false);
                anchorNode.setWorldRotation(nodeRotation, false);

                anchorNode.setParent(anchorNode.getSceneViewInternal());
                Anchor newAnchor = hitResult.createAnchor();
                anchorNode.setAnchor(newAnchor);
                anchorNode.forceWorldRotationToTarget();
                anchorNode.setRotationQuaternion(Quaternion.multiply(nodeRotation, getRotationFromPose(newAnchor.getPose()).inverted()));

//            Log.d("DADAYA", "endTransformation: " + Arrays.toString(newAnchor.getPose().getTranslation()));

            }

            desiredLocalPosition = Vector3.zero();
            getTransformableNode().setPosition(desiredLocalPosition);
//        desiredLocalRotation = calculateFinalDesiredLocalRotation(Quaternion.identity());
        }
    }

    private Vector3 getPositionFromPose(Pose pose) {
        return new Vector3(pose.tx(), pose.ty(), pose.tz());
    }

    private Quaternion getRotationFromPose(Pose pose) {
        return new Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw());
    }

    private ArNode getAnchorNodeOrDie() {
        BaseTransformableNode node = getTransformableNode();
//        NodeParent parent = node.getParent();
//        if (parent instanceof ArNode) {
//            return (ArNode) parent;
//        } else
        if (node instanceof ArNode) {
            return (ArNode) node;
        }
//        throw new IllegalStateException("TransformableNode must have an AnchorNode as a parent.");
        throw new IllegalStateException("TransformableNode must be a ArNode.");

    }

    private void updatePosition(FrameTime frameTime) {
        // Store in local variable for nullness static analysis.
        Vector3 desiredLocalPosition = this.desiredLocalPosition;
        if (desiredLocalPosition == null) {
            return;
        }

        Vector3 localPosition = getTransformableNode().getPosition();
//        Log.d("DADAYB", "updatePosition: " + localPosition.toString() + ", " + ((ArNode) getTransformableNode()).getWorldPosition().toString() + ", " + desiredLocalPosition.toString());
        float lerpFactor = MathHelper.clamp(frameTime.getDeltaSeconds() * LERP_SPEED, 0, 1);
        localPosition = Vector3.lerp(localPosition, desiredLocalPosition, lerpFactor);

        float lengthDiff = Math.abs(Vector3.subtract(desiredLocalPosition, localPosition).length());
        if (lengthDiff <= POSITION_LENGTH_THRESHOLD) {
            localPosition = desiredLocalPosition;
            this.desiredLocalPosition = null;
        }

        getTransformableNode().setPosition(localPosition);
    }

    private void updateRotation(FrameTime frameTime) {
        // Store in local variable for nullness static analysis.
        Quaternion desiredLocalRotation = this.desiredLocalRotation;
        if (desiredLocalRotation == null) {
            return;
        }

        Quaternion localRotation = getTransformableNode().getRotationQuaternion();
        float lerpFactor = MathHelper.clamp(frameTime.getDeltaSeconds() * LERP_SPEED, 0, 1);
        localRotation = Quaternion.slerp(localRotation, desiredLocalRotation, lerpFactor);

        float dot = Math.abs(dotQuaternion(localRotation, desiredLocalRotation));
        if (dot >= ROTATION_DOT_THRESHOLD) {
            localRotation = desiredLocalRotation;
            this.desiredLocalRotation = null;
        }

        getTransformableNode().setRotationQuaternion(localRotation);
    }

    /**
     * When translating, the up direction of the node must match the up direction of the plane from
     * the hit result. However, we also need to make sure that the original forward direction of the
     * node is respected.
     */
//    private Quaternion calculateFinalDesiredLocalRotation(Quaternion desiredLocalRotation) {
//        // Get a rotation just to the up direction.
//        // Otherwise, the node will spin around as you rotate.
//        Vector3 rotatedUp = Quaternion.rotateVector(desiredLocalRotation, Vector3.up());
//        desiredLocalRotation = Quaternion.rotationBetweenVectors(Vector3.up(), rotatedUp);
//
//        // Adjust the rotation to make sure the node maintains the same forward direction.
//        Quaternion forwardInLocal =
//                Quaternion.rotationBetweenVectors(Vector3.forward(), initialForward);
//        desiredLocalRotation = Quaternion.multiply(desiredLocalRotation, forwardInLocal);
//
//        return desiredLocalRotation.normalized();
//    }
    private static float dotQuaternion(Quaternion lhs, Quaternion rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z + lhs.w * rhs.w;
    }
}
