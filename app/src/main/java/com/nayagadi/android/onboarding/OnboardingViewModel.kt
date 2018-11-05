package com.nayagadi.android.onboarding

import androidx.lifecycle.ViewModel
import android.content.Context
import com.nayagadi.android.R

data class OnBoarding(val title: String, val body: String, val resId: Int)

class OnboardingViewModel : ViewModel() {

    val onboardingTitles = arrayOf(R.string.create_agent_account, R.string.agent_lead, R.string.agent_reward)
    val onboardingDetails = arrayOf(R.string.agent_details, R.string.agent_lead_info, R.string.agent_reward_info)

    val resourceIds = arrayOf(R.drawable.account_creation, R.drawable.find_customers, R.drawable.get_paid)

    fun getOnboardingDetails(context: Context): Array<OnBoarding> {
        val array = Array<OnBoarding>(onboardingTitles.size, { index ->
            OnBoarding(context.getString(onboardingTitles[index]), context.getString(onboardingDetails[index]), resourceIds[index])
        })
        return array
    }


}
