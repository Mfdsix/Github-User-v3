package com.zgenit.github_user.ui.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zgenit.github_user.MainActivity
import com.zgenit.github_user.R
import com.zgenit.github_user.broadcast.AlarmReceiver
import com.zgenit.github_user.helper.SessionHelper
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.include_toolbar.*

class SettingFragment : Fragment(), View.OnClickListener {

    private lateinit var session: SessionHelper
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.title_setting)

        session = SessionHelper((context as MainActivity))
        alarmReceiver = AlarmReceiver()
        setNotificationSwitch()

        sw_notification.setOnClickListener(this)
        tv_set_language.setOnClickListener(this)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setNotificationSwitch() {
        sw_notification.isChecked = session.dailyNotification
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_set_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
            R.id.sw_notification -> {
                session.dailyNotification = !session.dailyNotification
                if(session.dailyNotification){
                    alarmReceiver.setRepeatingAlarm((context as MainActivity))
                }else{
                    alarmReceiver.cancelAlarm((context as MainActivity))
                }
                setNotificationSwitch()
            }
        }
    }


}