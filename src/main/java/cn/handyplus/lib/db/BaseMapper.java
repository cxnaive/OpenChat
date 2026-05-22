package cn.handyplus.lib.db;

import java.util.List;
import java.util.Map;
import java.util.Optional;

interface BaseMapper<T> {
   int insert(T var1);

   boolean insertBatch(List<T> var1);

   Optional<T> selectOne();

   int count();

   int count(String var1);

   List<T> list();

   Page<T> page();

   int delete();

   int update();

   Optional<T> selectById(Integer var1);

   List<T> selectBatchIds(List<Integer> var1);

   List<Map<String, Object>> selectListMap();

   int updateById(Integer var1);

   int deleteById(Integer var1);

   int deleteBatchIds(List<Integer> var1);
}
