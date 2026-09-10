package com.tradefinance.demo.repository.oracle;

import java.util.List;
import java.util.Map;
import com.tradefinance.demo.dto.PiStep;
import com.tradefinance.demo.repository.oracle.typehandler.PlainBigDecimalTypeHandler;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

@Mapper
public interface PiMapper {
    /**
     * MyBatis writes the OUT parameter back into the mutable map passed in, under key "value" -
     * there is no clean typed return for a callable statement's OUT parameter.
     */
    @Select("""
            {#{value, mode=OUT, jdbcType=NUMERIC, javaType=string, typeHandler=com.tradefinance.demo.repository.oracle.typehandler.PlainBigDecimalTypeHandler}
                = call pi_pkg.calculate_pi(#{precision, mode=IN, jdbcType=NUMERIC})}
            """)
    @Options(statementType = StatementType.CALLABLE, timeout = 15)
    void calculatePi(Map<String, Object> params);

    @Select("""
            SELECT iteration, term_value, partial_sum
            FROM pi_intermediate_result ORDER BY iteration
            """)
    @ConstructorArgs({
            @Arg(column = "iteration", javaType = int.class),
            @Arg(column = "term_value", javaType = String.class, typeHandler = PlainBigDecimalTypeHandler.class),
            @Arg(column = "partial_sum", javaType = String.class, typeHandler = PlainBigDecimalTypeHandler.class)
    })
    List<PiStep> findIntermediateSteps();
}
