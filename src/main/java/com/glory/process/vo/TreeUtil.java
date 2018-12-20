package com.glory.process.vo;


import com.glory.process.util.MyCompare;
import com.glory.process.model.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ace on 2017/6/12.
 */
public class TreeUtil {
  private  int count = 0;
  private List<Menu> nodes;

  public TreeUtil(List<Menu> nodes){
    this.nodes = nodes;
  }

  public List<Menu> buildTree(){
    List<Menu> list = new ArrayList<Menu>();
    for (Menu node : nodes) {
      if (node.getParentId() == -1) {
        list.add(node);
      }
    }
    list = getSortChildren(list);
    for (Menu node : list) {
      build(node);
    }
    return list;
  }

  /***
   * 构建权限树
   * @Title: build
   * @Description: TODO
   * @param node
   */
  private void build(Menu node){
    List<Menu> children = getChildren(node);
    if (!children.isEmpty()) {
      node.setChildren(children);
      if(count <3){
        //children.get(0).setChecked("true");//设置默认选中
        count ++;
      }
      for (Menu child : children) {
        build(child);
      }
    }
  }
  /**
   *
   * @Title: getChildren
   * @Description: TODO 获取子节点
   * @param node
   * @return
   */
  private List<Menu> getChildren(Menu node){
    List<Menu> children = new ArrayList<Menu>();
    Integer id = node.getId();
    for (Menu child : nodes) {
      if (id==child.getParentId()) {
        children.add(child);
      }
    }
    return getSortChildren(children);
    // return children;
  }

  /**
   *
   * @Title: getChildren
   * @Description: TODO 获取排序子节点
   * @param
   * @return
   */
  private List<Menu> getSortChildren(List<Menu> children){
    MyCompare my = new MyCompare();
    Collections.sort(children,my) ;
    return children;
  }

  /**
   * 两层循环实现建树
   *
   * @param treeNodes 传入的树节点列表
   * @return
   */
  public static <T extends TreeNode> List<T> bulid(List<T> treeNodes, Object root) {

    List<T> trees = new ArrayList<T>();

    for (T treeNode : treeNodes) {

      if (root.equals(treeNode.getParentId())) {
        trees.add(treeNode);
      }

      for (T it : treeNodes) {
        if (it.getParentId() == treeNode.getId()) {
          if (treeNode.getChildren() == null) {
            treeNode.setChildren(new ArrayList<TreeNode>());
          }
          treeNode.add(it);
        }
      }
    }
    return trees;
  }

  /**
   * 使用递归方法建树
   *
   * @param treeNodes
   * @return
   */
  public static <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes, Object root) {
    List<T> trees = new ArrayList<T>();
    for (T treeNode : treeNodes) {
      if (root.equals(treeNode.getParentId())) {
        trees.add(findChildren(treeNode, treeNodes));
      }
    }
    return trees;
  }

  /**
   * 递归查找子节点
   *
   * @param treeNodes
   * @return
   */
  public static <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
    for (T it : treeNodes) {
      if (treeNode.getId() == it.getParentId()) {
        if (treeNode.getChildren() == null) {
          treeNode.setChildren(new ArrayList<TreeNode>());
        }
        treeNode.add(findChildren(it, treeNodes));
      }
    }
    return treeNode;
  }

}
