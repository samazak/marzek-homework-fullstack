package com.tradefinance.demo.repository.oracle;

import java.math.BigDecimal;
import com.tradefinance.demo.repository.AgeRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AgeMapper extends AgeRepository {
    @Override
    @Select("SELECT age_pkg.age_description(#{age}) FROM dual")
    String describeAge(@Param("age") BigDecimal age);
}
