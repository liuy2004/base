package cn.ly.service.base_framework.mysql.extend.special;

import cn.ly.base_common.support.datasource.annotation.Master;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * Created by liaomengge on 2019/11/20.
 */
@RegisterMapper
public interface SelectByExample4MasterMapper<T> {

    /**
     * 根据Example条件进行查询,只查询一条,LIMIT限制
     *
     * @param example
     * @return
     */
    @Master
    @SelectProvider(type = SelectByExample4MasterProvider.class, method = "dynamicSQL")
    T selectOneLimitByExample4Master(Object example);

    /**
     * 根据Example条件进行查询
     *
     * @param example
     * @return
     */
    @Master
    @SelectProvider(type = SelectByExample4MasterProvider.class, method = "dynamicSQL")
    List<T> selectByExample4Master(Object example);

    /**
     * 根据Example条件进行查询总数
     *
     * @param example
     * @return
     */
    @Master
    @SelectProvider(type = SelectByExample4MasterProvider.class, method = "dynamicSQL")
    int selectCountByExample4Master(Object example);
}
