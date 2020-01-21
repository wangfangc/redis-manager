package com.newegg.ec.redis.dao;

import com.newegg.ec.redis.entity.RDBAnalyzeResult;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author Kyle.K.Zhao
 * @date 1/8/2020 16:27
 */
@Mapper
public interface IRdbAnalyzeResult {

    @Delete("delete from rdb_analyze_result where id = #{id}")
    int deleteById(Long id);
    
    @Delete("delete from rdb_analyze_result where cluster_id = #{rdbAnalyzeResult.cluster_id} and schedule_id = #{rdbAnalyzeResult.schedule_id}")
    int deleteRdbAnalyzeResult(@Param("rdbAnalyzeResult") Map<String, Long> rdbAnalyzeResult);
    
    @Select("SELECT COUNT(*) FROM cluster")
    int selectCount();

    @Select("SELECT * FROM rdb_analyze_result WHERE id = #{id}")
    RDBAnalyzeResult selectByResultId(Long id);

    @Insert("INSERT INTO rdb_analyze_result(schedule_id, cluster_id, result, analyze_config) " +
            "VALUES (#{scheduleId}, #{clusterId}, #{result}, #{analyzeConfig})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Integer insert(RDBAnalyzeResult rdbAnalyzeResult);

    @Select("SELECT * FROM rdb_analyze_result")
    List<RDBAnalyzeResult> selectList();

    @Update("Update rdb_analyze_result set result=#{result} where id=#{id} ")
    Integer updateResult(RDBAnalyzeResult rdbAnalyzeResult);

    @Delete("delete from rdb_analyze_result where schedule_id IN (select min(schedule_id) from rdb_analyze_result)")
    void deleteOld();

    /**
     * query info from db by id and max schedule_id
     * @param cluster_id queryId
     * @return RDBAnalyzeResult
     */
    @Select("select * from rdb_analyze_result where schedule_id=(select max(schedule_id) from rdb_analyze_result where cluster_id = #{cluster_id})")
    @Results({ @Result(id = true, column = "id", property = "id"),
            @Result(column = "schedule_id", property = "scheduleId"),
            @Result(column = "cluster_id", property = "clusterId"),
            @Result(column = "result", property = "result"),
            @Result(column = "analyze_config", property = "analyzeConfig")})
    RDBAnalyzeResult selectLatestResultByRedisInfoId(Long cluster_id);

    /**
     * query all result by redis_info_id
     * @param cluster_id queryId
     * @return List<RDBAnalyzeResult>
     */
    @Select("select * from rdb_analyze_result where cluster_id= #{cluster_id} ORDER BY schedule_id DESC LIMIT 0,7")
    List<RDBAnalyzeResult> selectAllResultByClusterId(Long cluster_id);

    /**
     * query all result by redis_info_id
     * @param cluster_id queryId
     * @return List<RDBAnalyzeResult>
     */
    @Select("select * from rdb_analyze_result where schedule_id != (select max(schedule_id) from rdb_analyze_result where cluster_id = #{cluster_id}) and cluster_id = #{cluster_id}")
    @Results({ @Result(id = true, column = "id", property = "id"),
            @Result(column = "schedule_id", property = "scheduleId"),
            @Result(column = "cluster_id", property = "clusterId"),
            @Result(column = "result", property = "result"),
            @Result(column = "analyze_config", property = "analyzeConfig")})
    List<RDBAnalyzeResult> selectAllResultByIdExceptLatest(Long cluster_id);


    /**
     * query all result by redis_info_id
     * @param clusterId queryId
     * @param resultId queryId
     * @return List<RDBAnalyzeResult>
     */
    @Select("SELECT * FROM `rdb_analyze_result` WHERE id != #{resultId} AND cluster_id = #{clusterId} AND schedule_id" +
            " < #{scheduleId} ORDER BY " +
            "schedule_id DESC " +
            "LIMIT 0,6")
    @Results({ @Result(id = true, column = "id", property = "id"),
            @Result(column = "schedule_id", property = "scheduleId"),
            @Result(column = "cluster_id", property = "clusterId"),
            @Result(column = "result", property = "result"),
            @Result(column = "analyze_config", property = "analyzeConfig")})
    List<RDBAnalyzeResult> selectRecentlyResultByIdExceptSelf(Long resultId, Long clusterId, Long scheduleId);



    @Select("create TABLE IF NOT EXISTS `rdb_analyze_result`( " +
            "id integer AUTO_INCREMENT, " +
            "schedule_id bigint NOT NULL, " +
            "cluster_id integer, " +
            "result longtext, " +
            "analyze_config longtext," +
            "PRIMARY KEY (id) " +
            ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;")
    void createRdbAnalyzeResult();


    @Select("select * from rdb_analyze_result where schedule_id= #{scheduleId} and cluster_id = #{cluster_id}")
    RDBAnalyzeResult selectByRedisIdAndSId(Long cluster_id, Long scheduleId);
}
