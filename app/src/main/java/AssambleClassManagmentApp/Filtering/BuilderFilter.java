package AssambleClassManagmentApp.Filtering;

public class BuilderFilter{

    public static Filter buildFilter(FilterSetting setting){
        Filter filter = new NonFilter();
        if(setting.actual){
            filter = new ActualTaskFilter(filter, setting.nonComplete);
        }
        if(setting.priority_tasks != null){
            filter = new PriorityTaskFilter(filter, setting.priority_tasks);
        }
        if(setting.tags != null){
            filter = new TagTaskFilter(filter, setting.tags);
        }
        return filter;
    }

}
