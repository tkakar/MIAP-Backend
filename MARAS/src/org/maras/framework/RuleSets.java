package org.maras.framework;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 * This class represent a set of MARAS association rules. RuleSets can be used to find
 * closures (filters rules to ensure that the itemset crated by the rules is a closure).
 *
 * This class is an extension of the AssocRules class from the SPMF library put together by Philippe Fournier-Viger.
 * 
 * @see AssocRules
 * @see InteractionSets
 * @see Rule
 * @author Brian Zylich
 */
public class RuleSets extends AssocRules {

	/**
	 * Constructor
	 * @param name  a name for this list of association rules (string)
	 */
	public RuleSets(String name)
	{
		super(name);
	}

	/**
	 * Constructor
	 * @param name  a name for this list of association rules (string)
	 * @param rules rules generated by the association rule mining algorithm
	 */
	public RuleSets(String name, AssocRules rules) {
		super(name);
		//add existing rules to the RuleSets
        for (AssocRule rule : rules.getRules()) {
            List<Item> ante = new ArrayList<>();
            int[] itemset1 = rule.getItemset1();
            for (int a = 0; a < itemset1.length; a++) {
                ante.add(Item.fromInt(itemset1[a]));
            }
            List<Item> cons = new ArrayList<>();
            int[] itemset2 = rule.getItemset2();
            for (int c = 0; c < itemset2.length; c++) {
                cons.add(Item.fromInt(itemset2[c]));
            }

            this.addRule(new Rule(ante, cons, rule.getCoverage(), rule.getAbsoluteSupport(), rule.getConfidence(), rule.getLift()));
        }
	}

	/**
	 * Filters the rules to ensure that the itemset created by the rules is a closure.
	 * 
	 * @param closures
	 *            The InteractionSets object containing all closed InteractionSet's.
	 * @return RuleSets object containing all of the closed rules.
	 */
	public RuleSets findClosures(InteractionSets closures)
	{
		RuleSets closedRules = new RuleSets("Closed Rules");
		for (AssocRule rule : this.getRules()) {
			Interaction ruleInteraction = ((Rule)rule).getInteraction();
			if(closures.getLevels().size() > (ruleInteraction.size())) {
				boolean found = false;
				for(Itemset itemset: closures.getLevels().get(ruleInteraction.size())) {
					Set<Item> items = ((Interaction) itemset).getInteractions();
					if(items.containsAll(ruleInteraction.getInteractions())) {
						found = true;
						break;
					}
				}
				if(found) {
					closedRules.addRule(rule);
				}
			}
		}
		return closedRules;
	}

	/**
	 * Filters the rules to ensure that the antecedent only contains drugs and the consequent only
	 * contains reactions.
	 * 
	 * @return RuleSets object containing all of the filtered rules.
	 */
	public RuleSets filterRules() {
		RuleSets filteredRules = new RuleSets("Filtered Rules");
		for (AssocRule arule : this.getRules()) {
			boolean flag = false;
			Rule r = (Rule)arule;
			List<Item> ruleAnte = r.getAnte();
			for (Item aItem : ruleAnte) {
				if (!aItem.isDrug()) {
					flag = true;
				}
			}
			List<Item> ruleCons = r.getCons();
			for (Item cItem : ruleCons) {
				if (cItem.isDrug()) {
					flag = true;
				}
			}
			if(!flag) {
				filteredRules.addRule(arule);
			}
		}

		return filteredRules;
	}
	
	/**
	 * Filters rules to remove rules with <2 drugs
	 * @return RuleSets object without any rules that have less than 2 drugs
	 */
	public RuleSets filterNoSingletonRules() {
		RuleSets filteredRules = new RuleSets("Filtered Rules - No singletons");
		for (AssocRule arule : this.getRules()) {
			boolean flag = false;
			Rule r = (Rule)arule;
			List<Item> ruleAnte = r.getAnte();
			if (ruleAnte.size() < 2) {
				flag = true;
			}
			if(!flag) {
				filteredRules.addRule(arule);
			}
		}

		return filteredRules;
	}
	
	/**
	 * Filters rules to remove rules with >2 drugs or >1 ADR
	 * @return RuleSets object without any rules that have more than 2 drugs or more than 1 adr
	 */
	public RuleSets filterNoComplexRules() {
		RuleSets filteredRules = new RuleSets("Filtered Rules - No singletons");
		for (AssocRule arule : this.getRules()) {
			boolean flag = false;
			Rule r = (Rule)arule;
			List<Item> ruleAnte = r.getAnte();
			List<Item> ruleCons = r.getCons();
			if (ruleAnte.size() > 2 || ruleCons.size() > 1) {
				flag = true;
			}
			if(!flag) {
				filteredRules.addRule(arule);
			}
		}

		return filteredRules;
	}
}
