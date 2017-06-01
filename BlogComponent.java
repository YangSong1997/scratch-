
@ParametersInfo(type=BlogComponent.BlogComponentInfo.class)
public class BlogComponent extends BaseHstComponent{
	
	
	interface BlogComponentInfo {
		
		// title
		@Parameter(displayName = "Title", required = true, name = "title")
		public String getTitle();
		
		// filter
		@Parameter(displayName = "Filter",  name = "filter")
		public String getFilter();
		
		
		// sort order
		@Parameter(displayName = "Sort Order", name = "sortOrder")
		@DropDownList({"publication date descending","publication date ascending"})
		public String getSort();
		
		// display number
		@Parameter(displayName = "Display Number", name = "displayNumber",defaultValue = "10")
		public int getDisplay();			
	}

	
		
	

	@Override
	public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
		HstRequestContext context = request.getRequestContext();
		HippoBean relativeContentRoot = context.getSiteContentBaseBean().getBean("blogs");
		HstQueryManager hstQueryManager = context.getQueryManager();
		
		BlogComponentInfo blogComponentparams=getComponentParametersInfo(request);
		
		String blogTitle = blogComponentparams.getTitle();
		String blogFilter = blogComponentparams.getFilter();
		String blogSort = blogComponentparams.getSort();
		int blogDisplay = blogComponentparams.getDisplay();
		
		try{
			HstQuery query = hstQueryManager.createQuery(relativeContentRoot, Blog.class);
			
			//filter first
			// split blogFilter into array of strings
			String[] filterList = blogFilter.split(",");
			Filter filter=query.createFilter();	
			// for each item in the list
			for (int i = 0; i < filterList.length; i++) {
				Filter filter1 = query.createFilter();	
				filter1.addContains("hippocms:categories",filterList[i]);
				filter.addOrFilter(filter1);
			}
			query.setFilter((BaseFilter) filter);
			//query.toString()
			//query.getQueryAsString(true)
			
			//set for sort
			if (blogSort.equalsIgnoreCase("publication date ascending")){
				query.addOrderByAscending("hippocms:publicationDate");
			}
				
			if (blogSort.equalsIgnoreCase("publication date descending")){
				query.addOrderByDescending("hippocms:publicationDate");
			}
			
			//set limit
			query.setLimit((int) blogDisplay);
			
			HstQueryResult result=query.execute();
			request.setAttribute("results", result);
			
			// create the blogList where the length of blogList should not be greater than blogDisplay
			ArrayList<Blog> blogList = new ArrayList<Blog>();
			HippoBeanIterator itr = result.getHippoBeans();
			//int i = 0;&& i<blogDisplay
			while (itr.hasNext() ){
				Blog b=(Blog) itr.next();
				blogList.add(b);
				//i++;
			}
			if (!blogList.isEmpty()){
				request.setAttribute("blogList", blogList);
			}

			request.setAttribute("blogTitle",blogTitle);
			request.setAttribute("blogFilter",blogFilter);
			request.setAttribute("blogSort",blogSort);
			request.setAttribute("blogDisplay",blogDisplay);

		}
		catch(QueryException e){
			e.printStackTrace();
		}
		
		
	}
	
}
		
