package ricardo.com.fmuinovae;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class RadioGroupAdapter extends ArrayAdapter<Option> {

	Context context;
	int layoutResourceId;
	Option questions[] = null;

	public RadioGroupAdapter(Context context, int layoutResourceId,	Option[] questions) {
		super(context, layoutResourceId, questions);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.questions = questions;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MatrixHolder holder = null;
		final RadioButton[] rb = new RadioButton[5];

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new MatrixHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.heading);
			holder.group = (RadioGroup) row.findViewById(R.id.radio_group1);
			/*final RadioButton[] rb = new RadioButton[5];*/
	        for(int i=1; i<5; i++){
	            rb[i]  = new RadioButton(context);
	            //rb[i].setButtonDrawable(R.drawable.single_radio_chice);


				if(i == 1){
					rb[i].setId(i);
					rb[i].setText("Ruim");

				}else if(i == 2){
					rb[i].setId(i);
					rb[i].setText("Regular");

				}else if(i == 3){
					rb[i].setId(i);
					rb[i].setText("Bom");

				}else if(i == 4){
					rb[i].setId(i);
					rb[i].setText("Ótimo");
				}
				/*rb[i].setText(String.valueOf(i));*/

	            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(5, LayoutParams.WRAP_CONTENT);
	            params.weight=1.0f;
	            params.setMargins(15, 0, 2, 10);
	            holder.group.addView(rb[i],params); //the RadioButtons are added to the radioGroup instead of the layout
	        }
			row.setTag(holder);
		} else {
			holder = (MatrixHolder) row.getTag();
		}

		Option option = questions[position];
		holder.txtTitle.setText(option.title);


		holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			int totalEscolha = 0;
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				View radioButton = group.findViewById(checkedId);
				int radioId = group.indexOfChild(radioButton);

                if(radioId == 0){
					totalEscolha ++;
					RegisteringDataParticipation.idQuestionFirst=1;
					RegisteringDataParticipation.notaQuestionFirst=radioButton.getId();
					RegisteringDataParticipation.isUpLoadQuestion=RegisteringDataParticipation.isUpLoadQuestion+totalEscolha;
					//Log.e(" =================== Ruim", String.valueOf(radioButton.getId()));

				}else if(radioId == 1){
					RegisteringDataParticipation.idQuestionSecond=2;
					RegisteringDataParticipation.notaQuestionSecond=radioButton.getId();
					RegisteringDataParticipation.isUpLoadQuestion=RegisteringDataParticipation.isUpLoadQuestion+radioId;
					//Log.e(" =================== Regular", String.valueOf(radioButton.getId()));

				}else if(radioId == 2){
					RegisteringDataParticipation.idQuestionThird=3;
					RegisteringDataParticipation.notaQuestionThird=radioButton.getId();
					RegisteringDataParticipation.isUpLoadQuestion=RegisteringDataParticipation.isUpLoadQuestion+radioId;
					//Log.e(" =================== Bom", String.valueOf(radioButton.getId()));

				}else if(radioId == 3){
					RegisteringDataParticipation.idQuestionFourth=4;
					RegisteringDataParticipation.notaQuestionFourth=radioButton.getId();
					RegisteringDataParticipation.isUpLoadQuestion=RegisteringDataParticipation.isUpLoadQuestion+radioId;
					//Log.e(" =================== Otimo", String.valueOf(radioButton.getId()));
				}


			}
		});


		return row;
	}

	static class MatrixHolder {
		TextView txtTitle;
		RadioGroup group;
		int position;
	}
}
