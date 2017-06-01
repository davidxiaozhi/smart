package com.si.jupiter.smart.utils.zk;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.utils.PathUtils;

/**
 * Created by davidxiaozhi on 14-9-18.
 */
public class PathNode {
    private final String  path;
    private final String  node;
    private final Type evenType;

    public PathNode(String path, String node,Type evenType )
    {
        this.path = path;
        this.node = node;
        this.evenType = evenType;
    }

    public String getPath()
    {
        return path;
    }

    public String getNode()
    {
        return node;
    }

    public Type getEvenType() {
        return evenType;
    }

    @Override
    public String toString() {
        return "PathNode{" +
                "path='" + path + '\'' +
                ", node='" + node + '\'' +
                ", evenType=" + evenType +
                '}';
    }

    public static PathNode getPathAndNode(String path,Type type)
    {
        PathUtils.validatePath(path);
        int i = path.lastIndexOf('/');
        if ( i < 0 )
        {
            return new PathNode(path, "", type);
        }
        if ( (i + 1) >= path.length()  )
        {
            return new PathNode("/", "", type);
        }
        String node = path.substring(i + 1);
        String parentPath = (i > 0) ? path.substring(0, i) : "/";
        return new PathNode(parentPath, node, type);
    }

    public static  void  main(String[] args){
       System.out.println(PathNode.getPathAndNode("/aa/bb/dd", Type.CHILD_ADDED));
    }
}
