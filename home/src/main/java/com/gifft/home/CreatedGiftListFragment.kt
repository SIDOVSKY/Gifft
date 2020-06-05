package com.gifft.home

import dagger.Lazy
import javax.inject.Inject

class CreatedGiftListFragment @Inject constructor(
    newViewModel: Lazy<CreatedGiftListViewModel>
) : GiftListFragment(lazy { newViewModel.get() })
