package AssambleClassManagmentApp;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import AssambleClassManagmentApp.Filtering.BuilderFilter;
import AssambleClassManagmentApp.Filtering.Filter;
import AssambleClassManagmentApp.Filtering.FilterSetting;
import AssambleClassManagmentApp.Sorting.SortByIdTask;
import AssambleClassManagmentApp.Sorting.Sorter;
import by.ilagoproject.timeUp_ManagerTime.ManagerDB;

public class TaskManager implements ManagerDB.HandlerUpdateTaskInDb {

    private final Hashtable<Integer,AbsTask> tasks = new Hashtable<>();
    private final ManagerDB dbM;


    private ManagerDB.HandlerUpdateTaskInDb handlerUpdateTaskInDb;
    private Filter filter = BuilderFilter.buildFilter(new FilterSetting(null,null, false, false));
    private Sorter sorter = new SortByIdTask();


    public TaskManager(){
        dbM = ManagerDB.getManagerDB(null);
    }


    public void initTask(){
        initHabit();
        initDaily();
        initGoal();
    }

    public void initCheckListByTask(final AbsTask task){
        Cursor c = ManagerDB.getManagerDB(null).getCursorCheckListByTask(task.getId());
        final List<CheckTask> listCheck = task.getListUnderTaskChecked();
        while(c.moveToNext()){
            int id = c.getInt(c.getColumnIndex(ManagerDB.ID_COLUMN));
            String name = c.getString(c.getColumnIndex(ManagerDB.CHECKLISTTEXT_COLUMNNAME));
            boolean check =
                    c.getInt(c.getColumnIndex(ManagerDB.CHECKLISTTEXT_COLUMNNAME)) == 1;
            CheckTask checkTask = new CheckTask(id, name, check);
            listCheck.add(checkTask);
        }
    }

    public void initNotifyListByTask(final AbsTask task){
        Cursor c = ManagerDB.getManagerDB(null).getCursorNotifyByTask(task.getId());
        final List<NotificationTask> listNotify = task.getListNotify();
        while(c.moveToNext()){
            int id = c.getInt(c.getColumnIndex(ManagerDB.ID_COLUMN));
            String title = c.getString(c.getColumnIndex(ManagerDB.NOTIFYTITLE_COLUMNNAME));
            String message = c.getString(c.getColumnIndex(ManagerDB.NOTIFYMESSAGE_COLUMNNAME));
            long time = c.getLong(c.getColumnIndex(ManagerDB.NOTIFYTIME_COLUMNAME));
            long date = c.getLong(c.getColumnIndex(ManagerDB.NOTIFYDATE_COLUMNAME));
            NotificationTask notifyTask = new NotificationTask(id, title, message, date, time);
            listNotify.add(notifyTask);
        }
    }

    public void initDaily(){
        Cursor c = dbM.getCursorDaily();
        while(c.moveToNext()){
            Daily daily = (Daily) Daily.initTaskByCursor(c);
            TagManager.initTagByTask(daily);
            initCheckListByTask(daily);
            initNotifyListByTask(daily);
            tasks.put(daily.getId(),daily);
        }
    }

    public void initHabit(){
        Cursor c = dbM.getCursorHabit();
        while(c.moveToNext()){
            Habit habit = (Habit) Habit.initTaskByCursor(c);
            TagManager.initTagByTask(habit);
//            initCheckListByTask(habit);
            initNotifyListByTask(habit);
            tasks.put(habit.getId(),habit);
        }
    }

    public void initGoal(){
        Cursor c = dbM.getCursorGoal();
        while(c.moveToNext()){
            Goal goal = (Goal) Goal.initTaskByCursor(c);
            TagManager.initTagByTask(goal);
            initCheckListByTask(goal);
            initNotifyListByTask(goal);
            tasks.put(goal.getId(),goal);
        }
    }

    public AbsTask getById(int id){
        return tasks.get(id);
    }

    public void setHandlerUpdateTaskInDb(ManagerDB.HandlerUpdateTaskInDb handlerUpdateTaskInDb) {
        this.handlerUpdateTaskInDb = handlerUpdateTaskInDb;
    }

    public List<AbsTask> getTaskByType(AbsTask.Type_Task type){
        List<AbsTask> tasks = new ArrayList<>();
        for(AbsTask task : this.tasks.values()){
            if(task.TYPE == type)
                tasks.add(task);
        }
        filter.filter(tasks);
        sorter.sort(tasks);
        return tasks;
    }


    @Override
    public void notifyChange(int flag) {
        if((flag & ManagerDB.DELETE) == ManagerDB.DELETE){
            tasks.clear();
        }
        initTask();
        notifyHandler(flag);
    }

    public void notifyHandler(int flag){
        handlerUpdateTaskInDb.notifyChange(flag);
    }

    public Filter getFilter() {
        return filter;
    }

    public Sorter getSorter() {
        return sorter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        notifyHandler(ManagerDB.UPDATE);
    }

    public void setSorter(Sorter sorter) {
        this.sorter = sorter;
        notifyHandler(ManagerDB.UPDATE);
    }
}
