package com.si.jupiter.smart.resource;

public abstract class ResourceDefine<V> {
    private final ResourceConfig resourceConfig;
    private final ResourceOption<V> resourceKey;
    private volatile Resource<V> resource;
    public ResourceDefine(ResourceConfig config, ResourceOption<V> resourceKey) {
        this.resourceConfig = config;
        this.resourceKey = resourceKey;
    }
    //通过create方法回调build方法，避免子类重写该方法
    public final void makeResource() {
        try {
            resource = build(this.resourceConfig);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public final void destoryResource() {
        try {
            close(this.resource);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public final ResourceValidResult validResource() {
        try {
            return testUsability(this.resource);
        }catch (Exception e){
            e.printStackTrace();
            return ResourceValidResult.EMPTY;
        }
    }
    /**
     * 产生资源的键值对当中的key,通过泛型标注resource 类型
     * @return
     */
    public final ResourceOption<V> getResourceKey(){
        return this.resourceKey;
    }

    public final Resource<V> getResource(){
        if(this.resource==null){
            return Resource.EMPTY;
        }
        return this.resource;
    }

    /**
     * 构建resource
     * @param config
     * @return
     */
    public abstract Resource<V> build(ResourceConfig config);

    public abstract boolean close(Resource<V> resource);

    public abstract ResourceValidResult testUsability(Resource<V> resource);

}