/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: system online surveys Copyright (C) 2011
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.business.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.encuestame.core.service.AbstractBaseService;
import org.encuestame.core.service.imp.IFrontEndService;
import org.encuestame.core.util.ConvertDomainBean;
import org.encuestame.persistence.dao.SearchPeriods;
import org.encuestame.persistence.domain.HashTag;
import org.encuestame.persistence.domain.HashTagHits;
import org.encuestame.persistence.domain.survey.Poll;
import org.encuestame.persistence.domain.survey.PollHits;
import org.encuestame.persistence.domain.tweetpoll.TweetPoll;
import org.encuestame.persistence.domain.tweetpoll.TweetPollHits;
import org.encuestame.persistence.exception.EnMeNoResultsFoundException;
import org.encuestame.persistence.exception.EnMeSearchException;
import org.encuestame.utils.json.HomeBean;
import org.encuestame.utils.json.TweetPollBean;
import org.encuestame.utils.web.HashTagBean;
import org.encuestame.utils.web.PollBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Front End Service.
 * @author Picado, Juan juanATencuestame.org
 * @since Oct 17, 2010 11:29:38 AM
 * @version $Id:$
 */
@Service
public class FrontEndService extends AbstractBaseService implements IFrontEndService {

    /** Front End Service Log. **/
    private Logger log = Logger.getLogger(this.getClass());

    /** Max Results. **/
    private final Integer MAX_RESULTS = 15;

    /**
     * Search Items By tweetPoll.
     * @param maxResults limit of results to return.
     * @return result of the search.
     * @throws EnMeSearchException search exception.
     */
    public List<TweetPollBean> searchItemsByTweetPoll(
                final String period,
                final Integer start,
                Integer maxResults,
                final HttpServletRequest request)
                throws EnMeSearchException{
        final List<TweetPollBean> results = new ArrayList<TweetPollBean>();
        if(maxResults == null){
            maxResults = this.MAX_RESULTS;
        }
        log.debug("Max Results "+maxResults);
        final List<TweetPoll> items = new ArrayList<TweetPoll>();
        if (period == null ) {
            throw new EnMeSearchException("search params required.");
        } else {
            final SearchPeriods periodSelected = SearchPeriods.getPeriodString(period);
            if (periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast24(start, maxResults));
            } else if(periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast24(start, maxResults));
            } else if(periodSelected.equals(SearchPeriods.SEVENDAYS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast7Days(start, maxResults));
            } else if(periodSelected.equals(SearchPeriods.THIRTYDAYS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast30Days(start, maxResults));
            } else if(periodSelected.equals(SearchPeriods.ALLTIME)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndAllTime(start, maxResults));
            }
            log.debug("TweetPoll "+items.size());
            results.addAll(ConvertDomainBean.convertListToTweetPollBean(items));
            for (TweetPollBean tweetPoll : results) {
                tweetPoll = convertTweetPollRelativeTime(tweetPoll, request);
            }

        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#getFrontEndItems(java.lang.String, java.lang.Integer, java.lang.Integer, javax.servlet.http.HttpServletRequest)
     */
    public List<HomeBean> getFrontEndItems(final String period,
            final Integer start,
            Integer maxResults,
            final HttpServletRequest request) throws EnMeSearchException{
        final List<HomeBean> allItems = new ArrayList<HomeBean>();
        final List<TweetPollBean> tweetPollItems = this.searchItemsByTweetPoll(period, start, maxResults, request);
        allItems.addAll(ConvertDomainBean.convertTweetPollListToHomeBean(tweetPollItems));
        final List<PollBean> pollItems = this.searchItemsByPoll(period, start, maxResults);
        allItems.addAll(ConvertDomainBean.convertPollListToHomeBean(pollItems));
        return allItems;
    }

    /**
     * Search items by poll.
     * @param period
     * @param maxResults
     * @return
     * @throws EnMeSearchException
     */
    public List<PollBean> searchItemsByPoll(
            final String period,
            final Integer start,
            Integer maxResults)
            throws EnMeSearchException{
    final List<PollBean> results = new ArrayList<PollBean>();
    if(maxResults == null){
        maxResults = this.MAX_RESULTS;
    }
    final List<Poll> items = new ArrayList<Poll>();
    if(period == null ){
        throw new EnMeSearchException("search params required.");
    } else {
        final SearchPeriods periodSelected = SearchPeriods.getPeriodString(period);
        if(periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)){
            items.addAll(getFrontEndDao().getPollFrontEndLast24(start, maxResults));
        } else if(periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)){
            items.addAll(getFrontEndDao().getPollFrontEndLast24(start, maxResults));
        } else if(periodSelected.equals(SearchPeriods.SEVENDAYS)){
            items.addAll(getFrontEndDao().getPollFrontEndLast7Days(start, maxResults));
        } else if(periodSelected.equals(SearchPeriods.THIRTYDAYS)){
            items.addAll(getFrontEndDao().getPollFrontEndLast30Days(start, maxResults));
        } else if(periodSelected.equals(SearchPeriods.ALLTIME)){
            items.addAll(getFrontEndDao().getPollFrontEndAllTime(start, maxResults));
        }
        log.debug("Poll "+items.size());
        results.addAll(ConvertDomainBean.convertListToPollBean((items)));
    }

    return results;
    }

    /**
     * Get hashTags
     * @param maxResults
     * @param start
     * @return
     */
    public List<HashTagBean> getHashTags(
              Integer maxResults,
              final Integer start,
              final String tagCriteria){
        final List<HashTagBean> hashBean = new ArrayList<HashTagBean>();
        if(maxResults == null){
            maxResults = this.MAX_RESULTS;
        }
        final List<HashTag> tags = getHashTagDao().getHashTags(maxResults, start, tagCriteria);
        hashBean.addAll(ConvertDomainBean.convertListHashTagsToBean(tags));
        return hashBean;
    }

    /**
     * Get hashTag item.
     * @param tagName
     * @return
     * @throws EnMeNoResultsFoundException
     */
    public HashTag getHashTagItem(final String tagName) throws EnMeNoResultsFoundException {
        final HashTag tag = getHashTagDao().getHashTagByName(tagName);
        if (tag == null){
            throw new EnMeNoResultsFoundException("hashtag not found");
        }
        return tag;
    }

    /**
     * Get TweetPolls by hashTag id.
     * @param hashTagId
     * @param limit
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TweetPollBean> getTweetPollsbyHashTagId(
            final Long hashTagId,
            final Integer limit,
            final String filter,
            final HttpServletRequest request){
        final List<TweetPoll> tweetPolls = getTweetPollDao().getTweetpollByHashTagId(hashTagId, limit, filter);
        log.debug("TweetPoll by HashTagId total size ---> "+tweetPolls.size());
        final List<TweetPollBean> tweetPollBean = ConvertDomainBean.convertListToTweetPollBean(tweetPolls);
        for (TweetPollBean tweetPoll : tweetPollBean) {
            tweetPoll = convertTweetPollRelativeTime(tweetPoll, request);
        }
        return tweetPollBean;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#checkPreviousHashTagHit(java.lang.String)
     */
    public Boolean checkPreviousHashTagHit(final String ipAddress){
        boolean tagHit = false;
        final List<HashTagHits> hashTag = getFrontEndDao().getHashTagsHitByIp(ipAddress);
        try {
            if(hashTag.size() == 1){
                if(hashTag.get(0).getIpAddress().equals(ipAddress)){
                    tagHit = true;
                }
             }
            else if(hashTag.size() > 1){
                log.warn("");
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error(e);
        }
        return tagHit;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#checkPreviousTweetPollHit(java.lang.String)
     */
    public Boolean checkPreviousTweetPollHit(final String ipAddress){
        boolean hit = false;
        final List<TweetPollHits> tweetPollHits = new ArrayList<TweetPollHits>();
        try {
            if(tweetPollHits.size() == 1){
                if(tweetPollHits.get(0).getIpAddress().equals(ipAddress)){
                    hit = true;
                }
             }
            else if(tweetPollHits.size() > 1){
                log.warn("");
            }
        } catch (Exception e) {
            log.error(e);
        }
        return hit;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#checkPreviousPollHit(java.lang.String)
     */
    public Boolean checkPreviousPollHit(final String ipAddress){
        boolean hit = false;
        final List<PollHits> pollHits = new ArrayList<PollHits>();
        try {
            if(pollHits.size() == 1){
                if(pollHits.get(0).getIpAddress().equals(ipAddress)){
                    hit = true;
                }
             }
            else if(pollHits.size() > 1){
                log.warn("");
            }
        } catch (Exception e) {
            log.error(e);
        }
        return hit;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#registerHashTagHit(org.encuestame.persistence.domain.HashTag, java.lang.String)
     */
    public Boolean registerHashTagHit(final HashTag tag, final String ip) throws EnMeNoResultsFoundException{
        final HashTagHits hashHit ;
        Long hitCount = 1L;
        Boolean register = false;
        if ((ip != null) || (tag != null)) {
            hashHit = this.newHashTagHit(tag, Calendar.getInstance().getTime(), ip);
            if (hashHit != null) {
                hitCount = tag.getHits() + hitCount;
                tag.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(tag);
                register = true;
            }
        }
        return register;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#registerTweetPollHit(org.encuestame.persistence.domain.tweetpoll.TweetPoll, java.lang.String)
     */
    public Boolean registerTweetPollHit(final TweetPoll tweetPoll, final String ip) throws EnMeNoResultsFoundException{
        final TweetPollHits tweetPollHits ;
        Long hitCount = 1L;
        Boolean register = false;
        if ((ip != null) || (tweetPoll != null)) {
            tweetPollHits = this.newTweetPollHit(tweetPoll, Calendar.getInstance().getTime(), ip);
            if (tweetPollHits != null) {
                hitCount = tweetPoll.getHits() + hitCount;
                tweetPoll.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(tweetPoll);
                register = true;
            }
        }
        return register;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.imp.IFrontEndService#registerPollHit(org.encuestame.persistence.domain.survey.Poll, java.lang.String)
     */
    public Boolean registerPollHit(final Poll poll, final String ip) throws EnMeNoResultsFoundException{
        final PollHits pollHits ;
        Long hitCount = 1L;
        Boolean register = false;
        if ((ip != null) || (poll != null)) {
            pollHits = this.newPollHit(poll, Calendar.getInstance().getTime(), ip);
            if (pollHits != null) {
                hitCount = poll.getHits() + hitCount;
                poll.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(poll);
                register = true;
            }
        }
        return register;
    }

    /**
     * New hash tag hit.
     * @param tagName
     * @param hitDate
     * @param ipAddress
     * @return
     * @throws EnMeNoResultsFoundException
     */
    @Transactional(readOnly = false)
    private HashTagHits newHashTagHit(
            final HashTag tag,
            final Date hitDate,
            final String ipAddress) throws EnMeNoResultsFoundException {
        final HashTagHits tagHitsDomain = new HashTagHits();
        tagHitsDomain.setHitDate(hitDate);
        tagHitsDomain.setHashTag(tag);
        tagHitsDomain.setIpAddress(ipAddress);
        getFrontEndDao().saveOrUpdate(tagHitsDomain);
        return tagHitsDomain;
    }

    /**
     * New poll hit.
     * @param poll
     * @param hitDate
     * @param ipAddress
     * @return
     * @throws EnMeNoResultsFoundException
     */
    @Transactional(readOnly = false)
    private PollHits newPollHit(
            final Poll poll,
            final Date hitDate,
            final String ipAddress) throws EnMeNoResultsFoundException {
        final PollHits pollHitDomain = new PollHits();
        pollHitDomain.setHitDate(hitDate);
        pollHitDomain.setPoll(poll);
        pollHitDomain.setIpAddress(ipAddress);
        getFrontEndDao().saveOrUpdate(pollHitDomain);
        return pollHitDomain;
    }

    /**
     * New Tweetpoll hit.
     * @param tweetPoll
     * @param hitDate
     * @param ipAddress
     * @return
     * @throws EnMeNoResultsFoundException
     */
    @Transactional(readOnly = false)
    private TweetPollHits newTweetPollHit(
            final TweetPoll tweetPoll,
            final Date hitDate,
            final String ipAddress) throws EnMeNoResultsFoundException {
        final TweetPollHits tweetPollHitsDomain = new TweetPollHits();
        tweetPollHitsDomain.setHitDate(hitDate);
        tweetPollHitsDomain.setTweetPoll(tweetPoll);
        tweetPollHitsDomain.setIpAddress(ipAddress);
        getFrontEndDao().saveOrUpdate(tweetPollHitsDomain);
        return tweetPollHitsDomain;
    }
}
