package cn.handyplus.lib.db;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface DbFunction<T, R> extends Function<T, R>, Serializable {
}
