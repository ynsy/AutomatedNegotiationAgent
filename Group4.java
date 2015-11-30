import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.UtilitySpace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This is your negotiation party.
 */
public class Group4 extends AbstractNegotiationParty {
    private static final double MIN_UTILITY = 0.75;
	Bid bestBid = null;
    int currentRound = 0;
    int lastRoundForAccepting = 22;
    int average = 0;
    ArrayList<Double> allOfferedUtilitiesToMe = new ArrayList<Double>();
    ArrayList<Bid> allBids = new ArrayList<Bid>();
    HashMap<Double, Bid> bidsAndUtilities = new HashMap<Double, Bid>();
    SortedOutcomeSpace outcomeSpace;
    List<BidDetails> bids;
    //HashMap<Bid, Double> bidsAndUtilities = new HashMap<Bid, Double>();
   
    @Override
    public void init(UtilitySpace utilSpace,
            Deadline dl,
            TimeLineInfo tl,
            long randomSeed,
            AgentID agentId) {

    	super.init(utilSpace, dl, tl, randomSeed, agentId);
    	outcomeSpace = new SortedOutcomeSpace(utilitySpace);
    	bids  = outcomeSpace.getAllOutcomes();
    }
    
    @Override
    public Action chooseAction(List<Class<? extends Action>> validActions) {
        currentRound++;
        /*for (double i : allOfferedUtilitiesToMe) {
            average += i;
            average /= allOfferedUtilitiesToMe.size();
        }*/

        
        if (currentRound <= lastRoundForAccepting) {
            try {
                return new Offer(this.getBestOffer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentRound <= lastRoundForAccepting + 5) {
        	
        	//System.out.println(bidsAndUtilities);
        	
            double maxValueInMap = (Collections.max(bidsAndUtilities.keySet()));
            Bid bid = bidsAndUtilities.get(maxValueInMap);
            return new Offer(bid);
        } else {
            return new Accept();
        }
        return new Offer(generateRandomBid());
        
        
    }

    /**
     * All offers proposed by the other parties will be received as a message.
     * You can use this information to your advantage, for example to predict
     * their utility.
     *
     * @param sender
     *            The party that did the action.
     * @param action
     *            The action that party did.
     */

    @Override
    public void receiveMessage(Object sender, Action action) {
        super.receiveMessage(sender, action);
        // Here you hear other parties' messages

        if ((action instanceof Offer)) {
            Bid lastBid = ((Offer) action).getBid();
            try {
                this.utilitySpace.getUtility(lastBid);
                // diger robotların sana sundugu bid icin senin utility degerin
                if (this.utilitySpace.getUtility(lastBid) >= MIN_UTILITY) {
                //System.out.println("sender: " + sender + " " + this.utilitySpace.getUtility(lastBid));
                bidsAndUtilities.put(this.utilitySpace.getUtility(lastBid), lastBid);
                //bidsAndUtilities.put(lastBid, this.utilitySpace.getUtility(lastBid));
                //System.out.println("last bid: " + lastBid.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Bid getBestOffer() throws Exception {
        if (bestBid == null) {
        	bestBid = this.utilitySpace.getMaxUtilityBid();
        } else {
	        for (int i = 0; i < bids.size(); i++) {
	        	if (this.utilitySpace.getUtility(bestBid) > this.utilitySpace.getUtility(bids.get(i).getBid())) {
	        		bestBid = bids.get(i).getBid();
	        		//bidsAndUtilities.put(this.utilitySpace.getUtility(bestBid), bestBid);
	        		return bestBid;
	        	}
	        }
	        // System.out.println(bestBid.toString());
        }
        return bestBid;
    }

    @Override
    public String getDescription() {
        return "example party group 4";
    }

    public boolean isBidAcceptable(Bid bidOffered, Bid counterOffer) {
        try {
            if (counterOffer != null && utilitySpace.getUtility(bidOffered) >= utilitySpace.getUtility(counterOffer)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}