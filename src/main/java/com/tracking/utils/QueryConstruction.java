package com.tracking.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;

/**
 *
 * @author martin
 */
public class QueryConstruction {

    public static Query constructQuery(final String qStr) {

        final Query query = new Query();
        if (qStr == null || qStr.isEmpty()) {
            return query;
        }

        int start_pos = -1;

        int brk_count = 0;
        final List<Pair<Criteria, String>> prevCriteriasAndOperands = new ArrayList<>();

        String paramName = "";
        String operandName = "";
        String valueName = "";
        Criteria curCritera;

        for (int i = 0; i < qStr.length(); ++i) {
            if (qStr.charAt(i) == '(') {
                ++brk_count;
                continue;
            }
            if (qStr.charAt(i) == ' ') {
                if (start_pos != -1) {
                    if (!operandName.isEmpty()) {
                        valueName = qStr.substring(start_pos, i - 1);
                        curCritera = createCriteria(paramName, operandName, valueName);
                        paramName = "";
                        operandName = "";
                        valueName = "";
                    } else if (!paramName.isEmpty()) {
                        operandName = qStr.substring(start_pos, i-1);
                    } else {
                        paramName = qStr.substring(start_pos, i-1);
                    }
                }
                while (++i < qStr.length() && qStr.charAt(i) == ' ') {}
                start_pos = i;
            } else if (qStr.charAt(i) == ')') {
                if (!valueName.isEmpty()) {
                    
                }
            }
        }
        return query;
    }

    private static Criteria createCriteria(final String param, final String operand, final String value) {
        if ("eq".equals(operand)) {
            return Criteria.where(param).is(value);
        }
        if ("ne".equals(operand)) {
            return Criteria.where(param).ne(value);
        }
        if ("gt".equals(operand)) {
            return Criteria.where(param).gt(value);
        }
        if ("lt".equals(operand)) {
            return Criteria.where(param).lt(value);
        }
        System.err.println(String.format("The parameters havn't been casted to a criteria: param-%s, operand-%s, value-%s", param, operand, value));
        return null;
    }

    private static Criteria join(final Criteria c1, final Criteria c2, final String operand) {

        if ("or".equals(operand)) {
            return c1.orOperator(c2);
        }
        if ("and".equals(operand)) {
            return c1.andOperator(c2);
        }
        System.err.println(String.format("The operand for concatenation isn't supported: %s", operand));
        return null;
    }
}
