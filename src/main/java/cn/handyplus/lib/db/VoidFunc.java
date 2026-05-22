package cn.handyplus.lib.db;

@FunctionalInterface
public interface VoidFunc<P> {
   void call(P var1) throws Exception;
}
