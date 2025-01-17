package by.ilagoproject.timeUp_ManagerTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import java.util.List;
import AssambleClassManagmentTime.CheckTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import AssambleClassManagmentTime.TaskManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckListAdapter extends ArrayAdapter<CheckTask> {

    private final List<CheckTask> tasks;
    private final LayoutInflater inflater;
    private final ClickItemButton onClickEditCheck;
    private final ClickItemButton onClickDelCheck;

    private boolean editable = true;
    private boolean initCheckBox = false;


    private boolean updateList = false;

    public CheckListAdapter(@NonNull Context context, @NonNull List<CheckTask> objects, ClickItemButton editButton, ClickItemButton delButton) {
        super(context, 0, objects);
        tasks = objects;
        inflater = LayoutInflater.from(context);
        onClickEditCheck = editButton;
        onClickDelCheck = delButton;
    }

    @NonNull
    @Override
    public CheckTask getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public void remove(@Nullable CheckTask object) {
        tasks.remove(object);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setUpdateList(boolean updateList) {
        this.updateList = updateList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CheckTask checkTask = getItem(position);
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_check_list,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setText(checkTask.getText());
        holder.checkBox.setOnCheckedChangeListener(null); //crutches
        holder.checkBox.setChecked(checkTask.isCompleteTask());
        initCheckBox = checkTask.getId() > -1;
        holder.checkBox.setOnCheckedChangeListener((v,c)->{
           // check task
            checkTask.setCompleteTask(c);
            if(initCheckBox){
                ManagerDB.getManagerDB(null).updateChecklistCheck(checkTask.getId(), c ? 1 : 0); //crutches
                if(updateList) // crutches
                    TaskManager.getInstance(null).notifyHandler(ManagerDB.UPDATE);
            }
        });
        View finalConvertView = convertView;
        holder.editButton.setOnClickListener((v)-> onClickEditCheck.click(finalConvertView, parent, checkTask, this));
        holder.deleteButton.setOnClickListener((v)-> onClickDelCheck.click(finalConvertView, parent, checkTask, this));
        if(!editable){
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.editButton) Button editButton;
        @BindView(R.id.deleteButton) Button deleteButton;
        @BindView(R.id.checkbox) CheckBox checkBox;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface ClickItemButton{
        void click(View view, ViewGroup parent, CheckTask checkTask, CheckListAdapter adapterList);
    }
}
