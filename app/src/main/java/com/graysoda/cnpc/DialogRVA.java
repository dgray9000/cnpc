package com.graysoda.cnpc;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.graysoda.cnpc.activities.AssetChooser;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;

import java.util.ArrayList;

public class DialogRVA extends RecyclerView.Adapter<DialogRVA.IVH> {
    private final ArrayList fullList;
    private ArrayList filteredList = new ArrayList<>();
    private int button;

    public DialogRVA(ArrayList dialogChoiceList, int button) {
        fullList = dialogChoiceList;
        filteredList.addAll(dialogChoiceList);
        this.button = button;
    }

    @NonNull
    @Override
    public IVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IVH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_card_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IVH ivh, int i) {
    	Object obj = filteredList.get(i);

    	if (obj instanceof Asset){
    		String text = ((Asset) obj).getSymbol() + " - " + ((Asset) obj).getName();
			ivh.textView.setText(text);
		} else if (obj instanceof Exchange){
			ivh.textView.setText(((Exchange)obj).getName());
		} else if (obj instanceof String){
    		ivh.textView.setText((String) obj);
		}


        ivh.cv.setOnClickListener(v -> {
			AssetChooser.setChosen(obj, button);
		});
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String filterText){
        filteredList.clear();

        if (filterText.isEmpty()){
            filteredList.addAll(fullList);
        } else {
            for (Object obj : fullList){
            	if (obj instanceof Asset){
            		String listText = ((Asset) obj).getSymbol() + " - " + ((Asset) obj).getName();
            		if (listText.toLowerCase().contains(filterText.toLowerCase())){
            			filteredList.add(obj);
					}
				} else if (obj instanceof Exchange){
					if (((Exchange) obj).getName().toLowerCase().contains(filterText.toLowerCase())){
						filteredList.add(obj);
					}
				} else if (obj instanceof String){
            		if (((String) obj).toLowerCase().contains(filterText.toLowerCase())){
            			filteredList.add(obj);
					}
				}
            }
        }

        notifyDataSetChanged();
    }

    public class IVH extends RecyclerView.ViewHolder{
    	CardView cv;
        TextView textView;
        public IVH(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.dialog_cardView);
            textView = itemView.findViewById(R.id.dialog_card_text);
        }
    }
}
