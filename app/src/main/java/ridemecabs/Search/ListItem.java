/*
* Defines a simple object to be displayed in a list view.
*/
package ridemecabs.Search;
    
public class ListItem {
   	public String title;
   	public String subTitle;
    	
   	// default constructor
   	public ListItem() {
   		this("Title", "Subtitle");
   	}
    	
   	// main constructor
   	public ListItem(String title, String subTitle) {
   		super();
   		this.title = title;
   		this.subTitle = subTitle;
   	}
    	
   	// String representation
   	public String toString() {
   		return this.title + " : " + this.subTitle;
   	}
}