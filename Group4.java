import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
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
    int lastRoundForAccepting = 100;
    int average = 0;
    ArrayList<Double> allOfferedUtilitiesToMe = new ArrayList<Double>();
    ArrayList<Bid> allBids = new ArrayList<Bid>();
    HashMap<Double, Bid> bidsAndUtilities = new HashMap<Double, Bid>();
    SortedOutcomeSpace outcomeSpace;
    Group4BiddingStrategy biddingStrategy;
    TimeLineInfo tl;
   
    @Override
    public void init(UtilitySpace utilSpace,
            Deadline dl,
            TimeLineInfo tl,
            long randomSeed,
            AgentID agentId) {
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
        currentRound++;
        /*for (double i : allOfferedUtilitiesToMe) {
            average += i;
            average /= allOfferedUtilitiesToMe.size();
        }*/

        if (currentRound <= lastRoundForAccepting) {
            try {
                return new Offer(biddingStrategy.generateBidFromOutcomeSpace());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentRound <= lastRoundForAccepting * 5) {
        	try {
				return new Offer(biddingStrategy.getRandomFromOutcomeSpace());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
        	double maxValueInMap = (Collections.max(bidsAndUtilities.keySet()));
            Bid bid = bidsAndUtilities.get(maxValueInMap);
            try {
				if (this.utilitySpace.getUtility(bid) > this.utilitySpace.getUtility(biddingStrategy.generateBidFromOutcomeSpace())) {
					return new Offer(bid);
            	} else {
					Bid offeredBid = bidsAndUtilities.get(maxValueInMap);
	            	return new Offer(offeredBid);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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