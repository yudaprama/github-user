import React, {Component} from 'react';
import {DeviceEventEmitter, NativeAppEventEmitter, Platform} from 'react-native';
import {isIOS} from 'constants';
import Spasi from 'uppercase-space';
import {sample} from 'lodash';
import {
	dismissAllModals,
	dismissModal,
	getCurrentlyVisibleScreenId,
	navigatorPop,
	navigatorPopToRoot,
	navigatorPush,
	navigatorResetTo,
	navigatorSetButtons,
	navigatorSetDrawerEnabled,
	navigatorSetStyle,
	navigatorSetSubtitle,
	navigatorSetTabBadge,
	navigatorSetTabButton,
	navigatorSetTitle,
	navigatorSetTitleImage,
	navigatorSwitchToTab,
	navigatorSwitchToTopTab,
	navigatorToggleDrawer,
	navigatorToggleNavBar,
	navigatorToggleTabs,
	showContextualMenu,
	showModal
} from "./models";
import {clearEventHandler, setEventHandler} from "./index";

class Navigator {
  constructor(navigatorID, navigatorEventID, screenInstanceID) {
    this.navigatorID = navigatorID;
    this.screenInstanceID = screenInstanceID;
    this.navigatorEventID = navigatorEventID;
    this.navigatorEventHandler = null;
    this.navigatorEventHandlers = [];
    this.navigatorEventSubscription = null;
  }

  push(params = {}) {
    return navigatorPush(this, params);
  }

  navigate(params = {}) {
    params = {
      title: params.title ? params.title : Spasi(params.screen),
      backButtonTitle: "",
      ...params
    };
    if (isIOS) {
      navigatorToggleTabs(this, {
        to: 'hidden',
        animated: true
      });
      navigatorPush(this, params);
    } else {
      if (!params.animationType) {
        params.animationType = sample(['slide-horizontal','fade'])
      }
      showModal(params);
    }
  }

  pop(params = {}) {
    return navigatorPop(this, params);
  }

  goBack(params = {}) {
    if (isIOS) {
      return navigatorPop(this, params);
    } else {
      return dismissModal(params);
    }
  }

  goBackToRoot(params = {}) {
    if (isIOS) {
      return navigatorPopToRoot(this, params);
    } else {
      return dismissAllModals(params);
    }
  }

  popToRoot(params = {}) {
    return navigatorPopToRoot(this, params);
  }

  resetTo(params = {}) {
    return navigatorResetTo(this, params);
  }

  setButtons(params = {}) {
    return navigatorSetButtons(this, this.navigatorEventID, params);
  }

  setTitle(params = {}) {
    return navigatorSetTitle(this, params);
  }

  setSubTitle(params = {}) {
    return navigatorSetSubtitle(this, params);
  }

  setTitleImage(params = {}) {
    return navigatorSetTitleImage(this, params);
  }

  setStyle(params = {}) {
    return navigatorSetStyle(this, params);
  }

  toggleDrawer(params = {}) {
    return navigatorToggleDrawer(this, params);
  }

  setDrawerEnabled(params = {}) {
    return navigatorSetDrawerEnabled(this, params);
  }

  toggleTabs(params = {}) {
    return navigatorToggleTabs(this, params);
  }

  toggleNavBar(params = {}) {
    return navigatorToggleNavBar(this, params);
  }

  setTabBadge(params = {}) {
    return navigatorSetTabBadge(this, params);
  }

  setTabButton(params = {}) {
    return navigatorSetTabButton(this, params);
  }

  switchToTab(params = {}) {
    return navigatorSwitchToTab(this, params);
  }

  switchToTopTab(params = {}) {
    return navigatorSwitchToTopTab(this, params);
  }

  showContextualMenu(params, onButtonPressed) {
    return showContextualMenu(this, params, onButtonPressed);
  }

  onNavigationEvent(callback) {
    this.navigatorEventHandler = callback;
	  if (!this.navigatorEventSubscription) {
		  let Emitter = Platform.OS === 'android' ? DeviceEventEmitter : NativeAppEventEmitter;
		  this.navigatorEventSubscription = Emitter.addListener(this.navigatorEventID, (event) => this._onNavigatorEvent(event));
		  setEventHandler(this.navigatorEventID, (event) => this._onNavigatorEvent(event));
	  }
  }

  _removeOnNavigatorEvent(callback) {
    const index = this.navigatorEventHandlers.indexOf(callback);
    if (index !== -1) {
      this.navigatorEventHandlers.splice(index, 1);
    }
  }

  _onNavigatorEvent(event) {
    if (this.navigatorEventHandler) {
      this.navigatorEventHandler(event);
    }
    this.navigatorEventHandlers.forEach(handler => handler(event));
  }

  cleanup() {
    if (this.navigatorEventSubscription) {
      this.navigatorEventSubscription.remove();
      this.navigatorEventHandlers = [];
      clearEventHandler(this.navigatorEventID);
    }
  }

  async screenIsCurrentlyVisible() {
    const res = await getCurrentlyVisibleScreenId();
    if (!res) {
      return false;
    }
    return res.screenId === this.screenInstanceID;
  }
}

class Screen extends Component {
  static navigationStyle = {};
  static navigationButtons = {};

  constructor(props) {
    super(props);
    if (props.navigatorID) {
      this.navigator = new Navigator(props.navigatorID, props.navigatorEventID, props.screenInstanceID);
    }
  }

  componentWillUnmount() {
    if (this.navigator) {
      this.navigator.cleanup();
      this.navigator = undefined;
    }
  }
}

export {
  Screen,
  Navigator
};
