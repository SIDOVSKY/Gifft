package com.gifft.home

import dagger.Lazy
import javax.inject.Inject

class ReceivedGiftListFragment @Inject constructor(
    newViewModel: Lazy<ReceivedGiftListViewModel>
) : GiftListFragment(lazy { newViewModel.get() })
