package com.ccy.android.wxplugin.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ccy.android.wxplugin.constant.Constant;

import java.util.List;

/**
 * 辅助服务操作类
 */
public class AccessOperation {

    private AccessibilityNodeInfo mRootNodeInfo;

    private AccessOperation() {
    }

    public static AccessOperation getInstance() {
        return Holder.ACCESS_OPERATION;
    }

    /**
     * 每个activity的accessibilityNodeInfo不同，所以每个页面都需要更新
     *
     * @param accessibilityNodeInfo nodeInfo
     */
    public void update(AccessibilityNodeInfo accessibilityNodeInfo) {
        mRootNodeInfo = accessibilityNodeInfo;
    }

    /**
     * 点击text文本
     *
     * @param text 文案
     * @return true事件响应成功 ,否则false
     */
    public boolean clickText(String text) {
        return performClick(text);
    }


    /**
     * 选择全部联系人
     *
     * @return 选中的人数
     */
    public int selectAllContact() {
        if (mRootNodeInfo != null) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = mRootNodeInfo.findAccessibilityNodeInfosByViewId(Constant.HORIZONTAL_SCROLL_VIEW_CHILD_ID);
            if (!accessibilityNodeInfoList.isEmpty()) {
                return accessibilityNodeInfoList.get(0).getChildCount();
            }
        }
        return 0;
    }

    /**
     * 给EditText设置文案前需先获取焦点
     *
     * @param id 控件id
     */
    public void requestFocus(String id) {
        if (mRootNodeInfo != null) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = mRootNodeInfo.findAccessibilityNodeInfosByViewId(id);
            if (!accessibilityNodeInfoList.isEmpty()) {
                // 让输入框获取焦点
                accessibilityNodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            }
        }
    }

    /**
     * 粘贴文案到EditText控件上
     * notes：需配合剪切板使用
     *
     * @param id 控件id
     */
    public void pasteText(String id) {
        if (mRootNodeInfo != null) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = mRootNodeInfo.findAccessibilityNodeInfosByViewId(id);
            if (!accessibilityNodeInfoList.isEmpty()) {
                // 粘贴剪切板上数据到控件上
                accessibilityNodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_PASTE);
            }
        }
    }

    /**
     * 设置文案到EditText上
     *
     * @param id   控件id
     * @param text 文案
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setText(String id, String text) {
        if (mRootNodeInfo != null) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = mRootNodeInfo.findAccessibilityNodeInfosByViewId(id);
            if (!accessibilityNodeInfoList.isEmpty()) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                // 将文案设置到EditText上
                accessibilityNodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        }
    }

    /**
     * 执行点击
     *
     * @param text 文案
     * @return true事件响应成功 ,否则false
     */
    private boolean performClick(String text) {
        if (mRootNodeInfo != null) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = mRootNodeInfo.findAccessibilityNodeInfosByText(text);
            // 由于activity可能对应多个相同的文案，所以返回的是一个列表
            for (AccessibilityNodeInfo nodeInfo : accessibilityNodeInfoList) {
                AccessibilityNodeInfo info = checkClickable(nodeInfo);
                if (info != null) {
                    // 执行点击操作
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 递归查询，查询文案所在控件是否可点击，不可点击时查询其父控件，直到找到可点击控件，返回控件AccessibilityNodeInfo，否则返回null
     *
     * @param nodeInfo 文案所对应AccessibilityNodeInfo
     * @return 可点击AccessibilityNodeInfo或则null
     */
    private AccessibilityNodeInfo checkClickable(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return null;
        }

        // 不可点击查询其父类并重新赋值
        if (!nodeInfo.isClickable()) {
            nodeInfo = checkClickable(nodeInfo.getParent());
        }

        return nodeInfo;
    }

    private static class Holder {
        private static final AccessOperation ACCESS_OPERATION = new AccessOperation();
    }
}
