package com.taobao.tddl.sqlobjecttree.oracle;

import static com.taobao.tddl.sqlobjecttree.oracle.SkipMaxUtils.buildRownumGroup;
import static com.taobao.tddl.sqlobjecttree.oracle.SkipMaxUtils.getRowNumMaxToInt;
import static com.taobao.tddl.sqlobjecttree.oracle.SkipMaxUtils.getRowNumSkipToInt;

import java.util.List;
import java.util.Set;

import com.taobao.tddl.interact.sqljep.Comparative;
import com.taobao.tddl.sqlobjecttree.Update;
import com.taobao.tddl.sqlobjecttree.WhereCondition;

public class OracleUpdate extends Update {
	public OracleUpdate() {
		super();
	}
	private Comparative rownumComparative = null;
	public StringBuilder regTableModifiable(Set<String> oraTabName,
			List<Object> list, StringBuilder sb) {
		super.appendUpdate(sb);
		sb = HintUtils.appendHint(oraTabName, list, sb, hints);
		sb = super.appendUpdateBody(oraTabName, list, sb);
		return sb;
	}

	@Override
	protected WhereCondition getWhereCondition() {
		return new OracleWhereCondition();
	}
	@Override
	public void init() {
		initAliasAndComparableMap(aliasToSQLFragementMap,repListMap);

		//���ExpressionGroup���ڴ��sql�����е�rownum Expression.Ĭ�϶�Ϊand��ϵ��������ʵ��and����orû��ϵ��
		//��Ϊ�����������ϵ�����顣
		rownumComparative =	buildRownumGroup(where.getExpGroup(),tbNames,aliasToSQLFragementMap);
		
		registerTraversalActionAndGet();
		
		registerUnmodifiableSqlOutputFragement();
	}


	public int getSkip(List<Object> param) {
		/*
		 * ���getSkip�ķ���ʵ���ϻᱻǶ�׵��á�
		 */
		// rownum>0 and rownum<10 row>0 nested rownum<10 ;rownum= bigdecimal
		// long int
		int temp = DEFAULT_SKIP_MAX;
		temp = getRowNumSkipToInt(rownumComparative.getVal(param, null));
		return temp;

	}

	protected int getRangeOrMax(List<Object> param) {
		throw new IllegalArgumentException("should not be here");
	}

	@Override
	public int getMax(List<Object> param) {
		int temp = DEFAULT_SKIP_MAX;
		temp = getRowNumMaxToInt(rownumComparative.getVal(param, null));
		return temp;
	}
}