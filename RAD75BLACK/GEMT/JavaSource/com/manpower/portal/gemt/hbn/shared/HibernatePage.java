package com.manpower.portal.gemt.hbn.shared;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

/**
 * @author MartinMuguiro
 */
public class HibernatePage implements Page {

  protected List elements;
  protected int pageSize;
  protected int pageNumber;
  protected int totalElements = 0;

  public static Set jdbcClassesSupportingScrollCursors = new HashSet();
  private ScrollableResults scrollableResults;

  private HibernatePage(int pageNumber, int pageSize) {
  	this.pageNumber = pageNumber;
  	this.pageSize = pageSize;
  }

  public boolean isFirstPage() {
    return getPageNumber() == 0;
  }

  public boolean isLastPage() {
    return getPageNumber() >= getLastPageNumber();
  }

  public boolean hasNextPage() {
  	
  	  return elements.size() > getPageSize();
  }

  public boolean hasPreviousPage() {
    return getPageNumber() > 0;
  }

  public int getLastPageNumber() {
    double totalResults = new Integer(getTotalNumberOfElements()).doubleValue();
    return new Double(Math.floor(totalResults / getPageSize())).intValue();
  }

  public List getThisPageElements() {
	/*
	* Since we retrieved one more than the specified pageSize when the
	* class was constructed, we now trim it down to the pageSize if a next
	* page exists.
	*/
  	if(hasNextPage() && elements == null){
  	
  		return null;
  		
  	}
  	
    return hasNextPage() ? elements.subList(0, getPageSize()) : elements;
  }

  public int getTotalNumberOfElements() {
    return totalElements;
  }

  public int getThisPageFirstElementNumber() {
    return getPageNumber() * getPageSize() + 1;
  }

  public int getThisPageLastElementNumber() {
    int fullPage = getThisPageFirstElementNumber() + getPageSize() - 1;
    return getTotalNumberOfElements() < fullPage ?
            getTotalNumberOfElements() :
            fullPage;
  }

  public int getNextPageNumber() {
    return getPageNumber() + 1;
  }

  public int getPreviousPageNumber() {
    return getPageNumber() - 1;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public static HibernatePage getHibernatePageInstance(Query query, Properties params, int pageNumber, int pageSize) {

      return getHibernatePageInstance(query, params, pageNumber, pageSize, ""/*ApplicationState.platformConfiguration.driver_class*/);
  }

  public static HibernatePage getHibernatePageInstance(Query query,
  													   Properties params,
                                                       int pageNumber,
                                                       int pageSize,
                                                       String driverClass)  {
    
  	if (jdbcClassesSupportingScrollCursors.contains(driverClass))
      return HibernatePage.getScrollPageInstanceWithTotalByScroll(query, params,pageNumber, pageSize);
    else
      return HibernatePage.getScrollPageInstanceWithTotalByList(query, params, pageNumber, pageSize);
  }

  /**
   * Construct a new HibernatePage. HibernatePage numbers are zero-based so the
   * first page is page 0.
   *
   * @param query      the Hibernate Query
   * @param pageNumber the page number (zero-based);
   *                   if Integer.MAX_VALUE will return the last page for the query
   * @param pageSize   the number of results to display on the page
   */
  protected static HibernatePage getScrollPageInstanceWithTotalByScroll(Query query, Properties params, int pageNumber, int pageSize) {

    HibernatePage sp = new HibernatePage(pageNumber, pageSize);
    try {
      sp.scrollableResults = query.scroll(ScrollMode.SCROLL_SENSITIVE);
      sp.scrollableResults.last();
      sp.totalElements = sp.scrollableResults.getRowNumber();

      sp.determineElements(query,params);

    } catch (HibernateException e) {
      System.out.println("Failed to create ScrollPage by getScrollPageInstanceWithTotalByScroll: " + e.getMessage());
    }

    return sp;
  }

  /**
   * Construct a new HibernatePage. HibernatePage numbers are zero-based so the
   * first page is page 0.
   *
   * @param query      the Hibernate Query
   * @param pageNumber the page number (zero-based);
   *                   if Integer.MAX_VALUE will return the last page for the query
   * @param pageSize   the number of results to display on the page
   */
  protected static HibernatePage getScrollPageInstanceWithTotalByList(Query query, Properties params, int pageNumber, int pageSize) {

    HibernatePage sp = new HibernatePage(pageNumber, pageSize);

    try {

      sp.scrollableResults = query.scroll(ScrollMode.FORWARD_ONLY);
      sp.totalElements = sp.calculateTotalElementsByList(query);

      sp.determineElements(query,params);

    } catch (HibernateException e) {
    	System.out.println("Failed to create ScrollPage by getScrollPageInstanceWithTotalByList: " + e.getMessage());
    }

    return sp;
  }

  private void determineElements(Query query, Properties params) throws HibernateException {

    if (Integer.MAX_VALUE == this.pageNumber)
      this.pageNumber = (getTotalNumberOfElements() / this.pageSize);

    /*
     * todo: this no more makes sense, as we know the total number of elements
     *
     * We set the max results to one more than the specfied pageSize to
     * determine if any more results exist (i.e. if there is a next page
     * to display). The result set is trimmed down to just the pageSize
     * before being displayed later (in getThisPageElements()).
     */
    
    Iterator iter =params.keySet().iterator();
	while(iter.hasNext()) {
		String stKey=(String)iter.next();
		String stVal=(String)params.get(stKey);
		query.setString(stKey,stVal);
	}
	
    elements = query.setFirstResult(this.pageNumber * this.pageSize).setMaxResults(this.pageSize + 1).list();
  }

  private int calculateTotalElementsByList(Query query) throws HibernateException {
    return query.list().size();
  }
  
  public boolean isDataValid(){
  	return elements != null;
  }
}