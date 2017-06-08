package com.si.jupiter.smart.resource;

import java.util.List;
import java.util.concurrent.*;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-6-5 下午4:48
 * Last Update:  17-6-5 下午4:48
 */
public class TestResourceOption{

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ResourceDefine<ExecutorService> serviceDefine = new ResourceDefine<ExecutorService>(null,new ResourceOption<ExecutorService>("thread1")) {
            @Override
            public Resource<ExecutorService> build(ResourceConfig config) {
             return  new Resource<ExecutorService>(Executors.newFixedThreadPool(1));
            }

            @Override
            public boolean close(Resource<ExecutorService> resource) {
                try {
                    resource.getValue().shutdown();
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public ResourceValidResult testUsability(Resource<ExecutorService> resource) {
                Callable<String> task = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "test1";
                    }
                };
                try {
                    Future<String>  result = resource.getValue().submit(task);
                    if("test1".equals(result.get())){
                        return new ResourceValidResult(true,getResourceKey(),"succ!!!");
                    }
                    else{
                        return new ResourceValidResult(false,getResourceKey(),"test fail!!");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return new ResourceValidResult(false,getResourceKey(),"test exception!! "+e.toString());
                }
            }
        };
        ResourceDefine<String> str1Resource = new ResourceDefine<String>(null,new ResourceOption<String>("str1")) {
            @Override
            public Resource<String> build(ResourceConfig config) {
                return new Resource<String>("str1");
            }

            @Override
            public boolean close(Resource<String> resource) {
                return true;
            }

            @Override
            public ResourceValidResult testUsability(Resource<String> resource) {
                return new ResourceValidResult(true,getResourceKey(),"succ!!!");
            }
        };

        ResourceBuilder builder =  ResourceManager.getResoureBuilder();
        builder.add(serviceDefine);
        builder.add(str1Resource);
        builder.build();
        List<ResourceValidResult> results = builder.testResourceUsablility();
        boolean isSucc = true ;
        for(ResourceValidResult validResult:results){
            if(!validResult.isUsable()){
                isSucc = false;
                System.out.println(validResult.getMessage());
                break;
            }
        }
        if(isSucc){
            System.out.println("启动成功");
            //全部成功测试一个线程池任务提交
            ExecutorService service = ResourceManager.getResource(serviceDefine.getResourceKey());
            Future<String> future =service.submit(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    return "new test task!!!";
                }
            });
            System.out.println("资源调用:"+future.get());
        }
        else{
            System.out.println("启动失败");
        }


        //测试结束停止全部资源
        builder.destoryAllResource();

    }
}
