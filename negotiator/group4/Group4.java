import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.UtilitySpace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This is your negotiation party.
 */
public class Group4 extends AbstractNegotiationParty {
	private static final double MIN_UTILITY = 0.75;
	private static final double TIME_LIMIT = 55;
	Bid bestBid = null;
	int currentRound = 0;
	int average = 0;
	Action opponentAction;
	Bid opponentBid = null;
	ArrayList<Double> allOfferedUtilitiesToMe = new ArrayList<Double>();
	ArrayList<Bid> allBids = new ArrayList<Bid>();
	HashMap<Double, Bid> bidsAndUtilities = new HashMap<Double, Bid>();
	SortedOutcomeSpace outcomeSpace;
	Group4BiddingStrategy biddingStrategy;
	TimeLineInfo tl;
	Random randGenerator = new Random();
	int count = 0;
	boolean initialStep = true;
	Bid randomValue = null;

	@Override
	public void init(UtilitySpace utilSpace, Deadline dl, TimeLineInfo tl, long randomSeed, AgentID agentId) {
		super.init(utilSpace, dl, tl, randomSeed, agentId);
		outcomeSpace = new SortedOutcomeSpace(utilitySpace);
		this.tl = tl;
		try {
			biddingStrategy = new Group4BiddingStrategy(outcomeSpace, utilSpace);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		try {
			bestBid = biddingStrategy.generateBidFromOutcomeSpace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (initialStep) {
			initialStep = false;
			return new Offer(bestBid);
		} else if (timeline.getCurrentTime() < TIME_LIMIT) {
			System.out.println("current time: " + timeline.getCurrentTime());
			Bid bid;
			double maxValueInMap = 0;

			if (!bidsAndUtilities.isEmpty()) {
				maxValueInMap = (Collections.max(bidsAndUtilities.keySet()));
				bid = bidsAndUtilities.get(maxValueInMap);
				return new Offer(bid);
			} else {
				return new Offer(bestBid);
			}
		} else if (isBidAcceptable(opponentBid)) {
			return new Accept();
		} else if (!bidsAndUtilities.isEmpty()) {
			System.out.println("randomdayým.." + timeline.getCurrentTime());
			getRandomFromMap();
			return new Offer(randomValue);
		} else {
			System.out.println("abi beste girdim.." + timeline.getCurrentTime());
			return new Offer(randomValue);
		}
	}

	@Override
	public void receiveMessage(Object sender, Action action) {
		super.receiveMessage(sender, action);
		// Here you hear other parties' messages

		if ((action instanceof Offer)) {
			this.opponentBid = ((Offer) action).getBid();
			Bid lastBid = Action.getBidFromAction((Action) action);
			try {
				if (this.utilitySpace.getUtility(lastBid) >= MIN_UTILITY) {
					bidsAndUtilities.put(this.utilitySpace.getUtility(lastBid), lastBid);
				} else {
					bidsAndUtilities.put(getUtility(getMinUtilRandomBid()), getMinUtilRandomBid());
					// bidsAndUtilities.put(getUtility(bestBid), bestBid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			opponentAction = action;
		}
	}

	@Override
	public String getDescription() {
		return "example party group 4";
	}

	public boolean isBidAcceptable(Bid bid) {
		if (getUtility(bid) >= MIN_UTILITY && getUtility(bid) >= getUtility(randomValue)) {
			return true;
		}
		return false;
	}

	protected Bid generateRandomBid() {
		Bid randomBid = null;
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();

		for (Issue currentIssue : issues) {
			try {
				values.put(currentIssue.getNumber(), getRandomValue(currentIssue));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			randomBid = new Bid(utilitySpace.getDomain(), values);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return randomBid;
	}

	public Bid getMinUtilRandomBid() {
		Bid b = generateRandomBid();
		while (true) {
			b = generateRandomBid();
			if (getUtility(b) >= MIN_UTILITY) {
				break;
			}
		}
		return b;
	}

	public Bid getRandomFromMap() {
		Random generator = new Random();
		Object[] values = bidsAndUtilities.values().toArray();
		randomValue = (Bid) values[generator.nextInt(values.length)];
		return randomValue;
	}
}