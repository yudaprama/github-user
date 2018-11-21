/*eslint-disable*/
import {Component} from 'react';
import {findNodeHandle} from 'react-native';
import {getRegisteredScreen} from '../index';
import Controllers, {Modal, Notification, ScreenUtils} from '../controllers';
import _ from 'lodash';

import PropRegistry from '../PropRegistry';

const React = Controllers.hijackReact();
const {
  ControllerRegistry,
  TabBarControllerIOS,
  NavigationControllerIOS,
  DrawerControllerIOS
} = React;

async function startTabBasedApp(params) {
  const controllerID = _.uniqueId('controllerID');
  params.tabs.map(function (tab, index) {
    const navigatorID = controllerID + '_nav' + index;
    const screenInstanceID = _.uniqueId('screenInstanceID');

    const components = tab.components;
    if (components) {
      params.tabs[index].components = components;
      Object.assign(tab, components[0]);
      components.shift();

      components.forEach(component => {
        const screenInstanceID = _.uniqueId('screenInstanceID');
  
        const {
          navigationStyle,
          navigationButtons,
          navigatorEventID
        } = _mergeScreenSpecificSettings(component.screen, screenInstanceID, params);
        _saveNavigatorButtonsProps(navigationButtons);
        _saveNavBarComponentProps(navigationStyle);
        const passProps = Object.assign({}, component.passProps);
        passProps.navigatorID = navigatorID;
        passProps.screenInstanceID = screenInstanceID;
        passProps.navigatorEventID = navigatorEventID;
  
  
        component.navigationParams = {
          screenInstanceID,
          navigationStyle,
          navigationButtons,
          navigatorEventID,
          navigatorID: navigatorID,
          passProps
        };
  
        component.subtitle = params.subtitle;
        component.passProps = passProps;
        component.navigationStyle = navigationStyle;

        savePassProps(component);
  
      });
        
    }

    const {
      navigationStyle,
      navigationButtons,
      navigatorEventID
    } = _mergeScreenSpecificSettings(tab.screen, screenInstanceID, tab);
    _saveNavigatorButtonsProps(navigationButtons);
    _saveNavBarComponentProps(navigationStyle);
    tab.navigationParams = {
      screenInstanceID,
      navigationStyle,
      navigationButtons,
      navigatorEventID,
      navigatorID
    };
  });

  const Controller = Controllers.createClass({
    render: function () {
      return (
        <TabBarControllerIOS
          id={controllerID + '_tabs'}
          style={params.tabsStyle}
          appStyle={params.appStyle}
          initialTabIndex={params.initialTabIndex}>
          {
            params.tabs.map(function (tab, index) {
              return (
                <TabBarControllerIOS.Item {...tab} title={tab.label}>
                  <NavigationControllerIOS
                    id={tab.navigationParams.navigatorID}
                    title={tab.title}
                    subtitle={tab.subtitle}
                    titleImage={tab.titleImage}
                    component={tab.screen}
                    components={tab.components}
                    passProps={{
                      navigatorID: tab.navigationParams.navigatorID,
                      screenInstanceID: tab.navigationParams.screenInstanceID,
                      navigatorEventID: tab.navigationParams.navigatorEventID,
                    }}
                    style={tab.navigationParams.navigationStyle}
                    leftButtons={tab.navigationParams.navigationButtons.leftButtons}
                    rightButtons={tab.navigationParams.navigationButtons.rightButtons}
                  />
                </TabBarControllerIOS.Item>
              );
            })
          }
        </TabBarControllerIOS>
      )
    }
  });
  savePassProps(params);
  _.set(params, 'passProps.timestamp', Date.now());

  ControllerRegistry.registerController(controllerID, () => Controller);
  return await ControllerRegistry.setRootController(controllerID, params.animationType, params.passProps || {});
}

async function startSingleScreenApp(params) {
  const components = params.components;
  let screen = params.screen;

  const controllerID = _.uniqueId('controllerID');
  const navigatorID = controllerID + '_nav';

  if (components) {
    screen = components[0];
    components.shift();

    components.forEach(component => {
      const screenInstanceID = _.uniqueId('screenInstanceID');

      const {
        navigationStyle,
        navigationButtons,
        navigatorEventID
      } = _mergeScreenSpecificSettings(component.screen, screenInstanceID, params);
      _saveNavigatorButtonsProps(navigationButtons);
      _saveNavBarComponentProps(navigationStyle);
      const passProps = Object.assign({}, params.passProps);
      passProps.navigatorID = navigatorID;
      passProps.screenInstanceID = screenInstanceID;
      passProps.navigatorEventID = navigatorEventID;


      component.navigationParams = {
        screenInstanceID,
        navigationStyle,
        navigationButtons,
        navigatorEventID,
        navigatorID: navigatorID,
        passProps
      };

      component.subtitle = params.subtitle;
      component.passProps = passProps;

      savePassProps(component);

    });
  }

  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigationStyle,
    navigationButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(screen.screen, screenInstanceID, screen);
  _saveNavigatorButtonsProps(navigationButtons);
  _saveNavBarComponentProps(navigationStyle);
  params.navigationParams = {
    screenInstanceID,
    navigationStyle,
    navigationButtons,
    navigatorEventID,
    navigatorID
  };

  const passProps = {
    navigatorID: navigatorID,
    screenInstanceID: screenInstanceID,
    navigatorEventID: navigatorEventID,
    ...screen.passProps
  };

  const Controller = Controllers.createClass({
    render: function () {
      if (!params.drawer || (!params.drawer.left && !params.drawer.right)) {
        return this.renderBody();
      } else {
        const navigatorID = controllerID + '_drawer';
        return (
          <DrawerControllerIOS id={navigatorID}
            componentLeft={params.drawer.left ? params.drawer.left.screen : undefined}
            passPropsLeft={{navigatorID: navigatorID}}
            componentRight={params.drawer.right ? params.drawer.right.screen : undefined}
            passPropsRight={{navigatorID: navigatorID}}
            disableOpenGesture={params.drawer.disableOpenGesture}
            type={params.drawer.type ? params.drawer.type : 'MMDrawer'}
            animationType={params.drawer.animationType ? params.drawer.animationType : 'slide'}
            style={params.drawer.style}
            appStyle={params.appStyle}
          >
            {this.renderBody()}
          </DrawerControllerIOS>
        );
      }
    },
    renderBody: function () {
      return (
        <NavigationControllerIOS
          id={navigatorID}
          title={screen.title}
          subtitle={params.subtitle}
          titleImage={screen.titleImage}
          component={screen.screen}
          components={components}
          passProps={passProps}
          style={navigationStyle}
          leftButtons={navigationButtons.leftButtons}
          rightButtons={navigationButtons.rightButtons}
          appStyle={params.appStyle}
        />
      );
    }
  });
  savePassProps(params);

  ControllerRegistry.registerController(controllerID, () => Controller);
  return await ControllerRegistry.setRootController(controllerID, params.animationType, params.passProps || {});
}

function _mergeScreenSpecificSettings(screenID, screenInstanceID, params) {
  const screenClass = getRegisteredScreen(screenID);
  const navigationStyle = Object.assign({}, screenClass.navigationStyle);
  if (params.navigationStyle) {
    Object.assign(navigationStyle, params.navigationStyle);
  }

  let navigatorEventID = screenInstanceID + '_events';
  let navigationButtons = _.cloneDeep(screenClass.navigationButtons);
  if (params.navigationButtons) {
    navigationButtons = _.cloneDeep(params.navigationButtons);
  }
  if (navigationButtons.leftButtons) {
    for (let i = 0; i < navigationButtons.leftButtons.length; i++) {
      navigationButtons.leftButtons[i].onPress = navigatorEventID;
    }
  }
  if (navigationButtons.rightButtons) {
    for (let i = 0; i < navigationButtons.rightButtons.length; i++) {
      navigationButtons.rightButtons[i].onPress = navigatorEventID;
    }
  }
  return {navigationStyle, navigationButtons, navigatorEventID};
}

function navigatorPush(navigator, params) {
  let previewViewID;
  const screenInstanceID = _.uniqueId('screenInstanceID');
  if (params.previewView instanceof Component) {
    previewViewID = findNodeHandle(params.previewView)
  } else if (typeof params.previewView === 'number') {
    previewViewID = params.previewView;
  } else if (params.previewView) {
    console.error('Navigator.push(params): params.previewView is not a valid react view');
  }
  const {
    navigationStyle,
    navigationButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  _saveNavigatorButtonsProps(navigationButtons);
  _saveNavBarComponentProps(navigationStyle);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigator.navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;
  passProps.previewViewID = previewViewID;
  passProps.isPreview = !!previewViewID;

  params.navigationParams = {
    screenInstanceID,
    navigationStyle,
    navigationButtons,
    navigatorEventID,
    navigatorID: navigator.navigatorID
  };

  savePassProps(params);

  Controllers.NavigationControllerIOS(navigator.navigatorID).push({
    title: params.title,
    subtitle: params.subtitle,
    titleImage: params.titleImage,
    component: params.screen,
    animated: params.animated,
    animationType: params.animationType,
    passProps: passProps,
    style: navigationStyle,
    backButtonTitle: params.backButtonTitle,
    backButtonHidden: params.backButtonHidden,
    leftButtons: navigationButtons.leftButtons,
    rightButtons: navigationButtons.rightButtons,
    previewViewID: previewViewID,
    previewActions: params.previewActions,
    previewHeight: params.previewHeight,
    previewCommit: params.previewCommit,
    timestamp: Date.now()
  });
}

function navigatorPop(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).pop({
    animated: params.animated,
    animationType: params.animationType,
    timestamp: Date.now()
  });
}

function navigatorPopToRoot(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).popToRoot({
    animated: params.animated,
    animationType: params.animationType
  });
}

function showAlertToast(text) {
  alert(text)
}

function navigatorResetTo(navigator, params) {
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigationStyle,
    navigationButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  _saveNavigatorButtonsProps(navigationButtons);
  _saveNavBarComponentProps(navigationStyle);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigator.navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;

  params.navigationParams = {
    screenInstanceID,
    navigationStyle,
    navigationButtons,
    navigatorEventID,
    navigatorID: navigator.navigatorID
  };

  savePassProps(params);

  Controllers.NavigationControllerIOS(navigator.navigatorID).resetTo({
    title: params.title,
    subtitle: params.subtitle,
    titleImage: params.titleImage,
    component: params.screen,
    animated: params.animated,
    animationType: params.animationType,
    passProps: passProps,
    style: navigationStyle,
    leftButtons: navigationButtons.leftButtons,
    rightButtons: navigationButtons.rightButtons
  });
}

function navigatorSetDrawerEnabled(navigator, params) {
  const controllerID = navigator.navigatorID.split('_')[0];
  Controllers.NavigationControllerIOS(controllerID + '_drawer').setDrawerEnabled(params)
}

function navigatorSetTitle(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setTitle({
    title: params.title,
    subtitle: params.subtitle,
    titleImage: params.titleImage,
    style: params.navigationStyle,
    isSetSubtitle: false
  });
}

function navigatorSetSubtitle(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setTitle({
    title: params.title,
    subtitle: params.subtitle,
    titleImage: params.titleImage,
    style: params.navigationStyle,
    isSetSubtitle: true
  });
}

function navigatorSetTitleImage(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setTitleImage({
    titleImage: params.titleImage
  });
}

function navigatorToggleNavBar(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setHidden({
    hidden: ((params.to === 'hidden') ? true : false),
    animated: params.animated
  });
}

function navigatorSetStyle(navigator, params) {
  _saveNavBarComponentProps(params);
  Controllers.NavigationControllerIOS(navigator.navigatorID).setStyle(params)
}

function navigatorToggleDrawer(navigator, params) {
  const controllerID = navigator.navigatorID.split('_')[0];
  if (params.to == 'open') {
    Controllers.DrawerControllerIOS(controllerID + '_drawer').open({
      side: params.side,
      animated: params.animated
    });
  } else if (params.to == 'closed') {
    Controllers.DrawerControllerIOS(controllerID + '_drawer').close({
      side: params.side,
      animated: params.animated
    });
  } else {
    Controllers.DrawerControllerIOS(controllerID + '_drawer').toggle({
      side: params.side,
      animated: params.animated
    });
  }
}

function navigatorToggleTabs(navigator, params) {
  const controllerID = navigator.navigatorID.split('_')[0];
  Controllers.TabBarControllerIOS(controllerID + '_tabs').setHidden({
    hidden: params.to == 'hidden',
    animated: !(params.animated === false)
  });
}

function navigatorSetTabBadge(navigator, params) {
  const controllerID = navigator.navigatorID.split('_')[0];
  if (params.tabIndex || params.tabIndex === 0) {
    Controllers.TabBarControllerIOS(controllerID + '_tabs').setBadge({
      tabIndex: params.tabIndex,
      badge: params.badge,
      badgeColor: params.badgeColor
    });
  } else {
    Controllers.TabBarControllerIOS(controllerID + '_tabs').setBadge({
      contentId: navigator.navigatorID,
      contentType: 'NavigationControllerIOS',
      badge: params.badge
    });
  }
}

function navigatorSetTabButton(navigator, params) {
  const controllerID = navigator.navigatorID.split('_')[0];
  if (params.tabIndex || params.tabIndex === 0) {
    Controllers.TabBarControllerIOS(controllerID + '_tabs').setTabButton({
      tabIndex: params.tabIndex,
      icon: params.icon,
      selectedIcon: params.selectedIcon,
      label: params.label,
    });
  } else {
    Controllers.TabBarControllerIOS(controllerID + '_tabs').setTabButton({
      contentId: navigator.navigatorID,
      contentType: 'NavigationControllerIOS',
      icon: params.icon,
      selectedIcon: params.selectedIcon,
      label: params.label,
    });
  }
}

function navigatorSwitchToTab(navigator, params) {
  const controllerID = navigator.navigatorID.split('_')[0];
  if (params.tabIndex || params.tabIndex === 0) {
    Controllers.TabBarControllerIOS(controllerID + '_tabs').switchTo({
      tabIndex: params.tabIndex
    });
  } else {
    Controllers.TabBarControllerIOS(controllerID + '_tabs').switchTo({
      contentId: navigator.navigatorID,
      contentType: 'NavigationControllerIOS'
    });
  }
}

function navigatorSetButtons(navigator, navigatorEventID, params) {
  _saveNavigatorButtonsProps(params);
  if (params.leftButtons) {
    const buttons = params.leftButtons.slice(); // clone
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].onPress = navigatorEventID;
    }
    Controllers.NavigationControllerIOS(navigator.navigatorID).setLeftButtons(buttons, params.animated);
  }
  if (params.rightButtons) {
    const buttons = params.rightButtons.slice(); // clone
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].onPress = navigatorEventID;
    }
    Controllers.NavigationControllerIOS(navigator.navigatorID).setRightButtons(buttons, params.animated);
  }
}

function showModal(params) {
  const controllerID = _.uniqueId('controllerID');
  const navigatorID = controllerID + '_nav';
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigationStyle,
    navigationButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  _saveNavigatorButtonsProps(navigationButtons);
  _saveNavBarComponentProps(navigationStyle);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;
  passProps.timestamp = Date.now();

  params.navigationParams = {
    screenInstanceID,
    navigationStyle,
    navigationButtons,
    navigatorEventID,
    navigatorID: navigator.navigatorID
  };

  const Controller = Controllers.createClass({
    render: function () {
      return (
        <NavigationControllerIOS
          id={navigatorID}
          title={params.title}
          subtitle={params.subtitle}
          titleImage={params.titleImage}
          component={params.screen}
          passProps={passProps}
          style={navigationStyle}
          leftButtons={navigationButtons.leftButtons}
          rightButtons={navigationButtons.rightButtons} />
      );
    }
  });

  savePassProps(params);

  ControllerRegistry.registerController(controllerID, () => Controller);
  Modal.showController(controllerID, params.animationType);
}

async function dismissModal(params = {}) {
  return await Modal.dismissController(params.animationType);
}

function dismissAllModals(params = {}) {
  Modal.dismissAllControllers(params.animationType);
}

function showLightBox(params) {
  const controllerID = _.uniqueId('controllerID');
  const navigatorID = controllerID + '_nav';
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigationStyle,
    navigationButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;

  params.navigationParams = {
    screenInstanceID,
    navigationStyle,
    navigationButtons,
    navigatorEventID,
    navigatorID
  };

  savePassProps(params);

  Modal.showLightBox({
    component: params.screen,
    passProps: passProps,
    style: params.style
  });
}

function dismissLightBox() {
  Modal.dismissLightBox();
}

function showInAppNotification(params) {
  const controllerID = _.uniqueId('controllerID');
  const navigatorID = controllerID + '_nav';
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigationStyle,
    navigationButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;

  params.navigationParams = {
    screenInstanceID,
    navigationStyle,
    navigationButtons,
    navigatorEventID,
    navigatorID
  };

  savePassProps(params);

  let args = {
    component: params.screen,
    passProps: passProps,
    style: params.style,
    animation: params.animation || Notification.AnimationPresets.default,
    position: params.position,
    shadowRadius: params.shadowRadius,
    dismissWithSwipe: params.dismissWithSwipe || true,
    autoDismissTimerSec: params.autoDismissTimerSec || 5
  };
  if (params.autoDismiss === false) delete args.autoDismissTimerSec;
  Notification.show(args);
}

function dismissInAppNotification(params) {
  Notification.dismiss(params);
}

function savePassProps(params) {
  //TODO this needs to be handled in a common place,
  //TODO also, all global passProps should be handled differently
  if (params.navigationParams && params.passProps) {
    PropRegistry.save(params.navigationParams.screenInstanceID, params.passProps);
  }

  if (params.screen && params.screen.passProps) {
    PropRegistry.save(params.screen.navigationParams.screenInstanceID, params.screen.passProps);
  }

  if (_.get(params, 'screen.topTabs')) {
    _.forEach(params.screen.topTabs, (tab) => savePassProps(tab));
  }

  if (params.tabs) {
    _.forEach(params.tabs, (tab) => {
      if (!tab.passProps) {
        tab.passProps = params.passProps;
      }
      savePassProps(tab);
    });
  }
}

function showContextualMenu() {
  // Android only
}

function dismissContextualMenu() {
  // Android only
}

async function getCurrentlyVisibleScreenId() {
  return await ScreenUtils.getCurrentlyVisibleScreenId();
}

function _saveNavBarComponentProps(navigationStyle) {
  if (navigationStyle.navBarCustomViewInitialProps) {
    const passPropsKey = _.uniqueId('navBarComponent');
    PropRegistry.save(passPropsKey, navigationStyle.navBarCustomViewInitialProps);
    navigationStyle.navBarCustomViewInitialProps = {passPropsKey};
  }
}

function _saveNavigatorButtonsProps({rightButtons, leftButtons}) {
  _saveNavigatorButtonsPassProps(rightButtons);
  _saveNavigatorButtonsPassProps(leftButtons);
}

function _saveNavigatorButtonsPassProps(buttons = []) {
  buttons.forEach((button) => {
    if (button.component) {
      const passPropsKey = _.uniqueId('customButtonComponent');
      PropRegistry.save(passPropsKey, button.passProps);
      button.passProps = {passPropsKey};
    }
  })
}

async function getLaunchArgs() {
  return await ControllerRegistry.getLaunchArgs();
}

export {
  startTabBasedApp,
  startSingleScreenApp,
  navigatorPush,
  navigatorPop,
  navigatorPopToRoot,
  navigatorResetTo,
  showModal,
  dismissModal,
  showAlertToast,
  dismissAllModals,
  showLightBox,
  dismissLightBox,
  showInAppNotification,
  dismissInAppNotification,
  navigatorSetButtons,
  navigatorSetDrawerEnabled,
  navigatorSetTitle,
  navigatorSetSubtitle,
  navigatorSetStyle,
  navigatorSetTitleImage,
  navigatorToggleDrawer,
  navigatorToggleTabs,
  navigatorSetTabBadge,
  navigatorSetTabButton,
  navigatorSwitchToTab,
  navigatorToggleNavBar,
  showContextualMenu,
  dismissContextualMenu,
  getCurrentlyVisibleScreenId,
  getLaunchArgs
};
