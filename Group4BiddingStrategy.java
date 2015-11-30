import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.utility.UtilitySpace;

public class Group4BiddingStrategy {

	private static final double MIN_UTILITY = 0.80;
	private SortedOutcomeSpace outcomeSpace;
	private List<Bid> possibleBids; 
	private Bid bestBid = null;
	private UtilitySpace utilitySpace;
	
	public Group4BiddingStrategy(SortedOutcomeSpace outcomeSpace, UtilitySpace utilitySpace) throws Exception {
		this.outcomeSpace = outcomeSpace;
		this.utilitySpace = utilitySpace;
		possibleBids = new ArrayList<Bid>();
		List<BidDetails> bidDetails = this.outcomeSpace.getAllOutcomes();
		for(int i = 0; i < bidDetails.size(); i++) {
			Bid bid = bidDetails.get(i).getBid();
			
			if (i == 0)
				bestBid = bid;
			if (utilitySpace.getUtility(bid) > MIN_UTILITY)
				possibleBids.add(bid);
				
		}
	}
	
	public Bid generateBidFromOutcomeSpace () throws Exception{
		if (bestBid == null) {
			bestBid = utilitySpace.getMaxUtilityBid();
			return bestBid;
		} else {
			for(int i = 0; i < possibleBids.size(); i++) {
				if (utilitySpace.getUtility(bestBid) > utilitySpace.getUtility(possibleBids.get(i)) && utilitySpace.getUtility(possibleBids.get(i)) >= MIN_UTILITY) {
	        		bestBid = possibleBids.get(i);
	        		return bestBid;
	        	}
			}
		}
		return bestBid;
	}
	
	public Bid getRandomFromOutcomeSpace() throws Exception {
		Random rand = new Random();
		return possibleBids.get(rand.nextInt(possibleBids.size() -1));
	}
}
