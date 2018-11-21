import React from 'react';
import {AppRegistry} from 'react-native';
import {Screen} from './Screen';
import PropRegistry from './PropRegistry';
import {isIOS} from "constants";
import Home from '../../screens/Home';
import Profile from '../../screens/Profile';
import {isAppLaunched, startTabBasedApp} from "./models";
import NativeEventsReceiver from "./NativeEventsReceiver";
import navigationStyle from "../../libs/navigationStyle";


/**
 * Register all screen component.
 * Here we register all screen by wrapping each internal component into our high order component
 * that interact with native react root view
 */

const registeredScreens = {};
const _allNavigatorEventHandlers = {};

function registerScreen(screenID, generator) {
	const generatorWrapper = function () {
		const InternalComponent = generator();

		return class extends Screen {
			static navigationStyle = InternalComponent.navigationStyle || {};
			static navigationButtons = InternalComponent.navigationButtons || {};

			constructor(props) {
				super(props);
				this.state = {
					internalProps: {...props, ...PropRegistry.load(props.screenInstanceID || props.passPropsKey)}
				}
			}

			componentWillReceiveProps(nextProps) {
				this.setState({
					internalProps: {...PropRegistry.load(this.props.screenInstanceID || this.props.passPropsKey), ...nextProps}
				})
			}

			render() {
				return (
					<InternalComponent testID={screenID} navigation={this.navigator} {...this.state.internalProps} />
				);
			}
		};
	};

	registeredScreens[screenID] = generatorWrapper;
	AppRegistry.registerComponent(screenID, generatorWrapper);

	return generatorWrapper;
}

registerScreen('Home', () => Home);
registerScreen('Profile', () => Profile);


function getRegisteredScreen(screenID) {
	const generator = registeredScreens[screenID];
	if (!generator) {
		console.error(`Navigation.getRegisteredScreen: ${screenID} used but not yet registered`);
		return undefined;
	}
	return generator();
}


/*
* Navigation Bridging Method
* This is were we define all navigation methods
*/

function startApp() {
	let params = {
		tabs: [{
			label: 'Home',
			title: 'Github Users',
			icon: require('../../assets/icoHome.png'),
			screen: 'Home',
			navigationStyle
		}, {
			label: 'Author',
			title: 'Author',
			icon: require('../../assets/icoProfile.png'),
			screen: 'Profile',
			navigationStyle
		}],
		animationType: 'fade'
	};

	let tabsStyle = {
		tabBarTranslucent: true,
		tabBarButtonColor: '#777777',
		tabBarSelectedButtonColor: 'orange'
	};

	if (isIOS) {
		params.tabsStyle = tabsStyle;
		params.appStyle = {
			orientation: 'portrait'
		};

		startTabBasedApp(params)

	} else {
		params.appStyle = {
			orientation: 'portrait',
			bottomTabBadgeBackgroundColor: 'rgb(104,126,132)',
			forceTitlesDisplay: true,
			...tabsStyle
		};

		isAppLaunched()
		.then(appLaunched => {
			if (appLaunched) {
				startTabBasedApp(params); // App is launched -> show UI
			} else {
				new NativeEventsReceiver().appLaunched(startTabBasedApp(params)); // App hasn't been launched yet -> show the UI only when needed.
			}
		});
	}
}

function setEventHandler(navigatorEventID, eventHandler) {
	_allNavigatorEventHandlers[navigatorEventID] = eventHandler;
}

function clearEventHandler(navigatorEventID) {
	delete _allNavigatorEventHandlers[navigatorEventID];
}

function broadcast({action, payload}) {
	if (!action) return;

	const event = {
		type: 'DeepLink',
		action,
		...(payload ? {payload} : {})
	};
	for (let i in _allNavigatorEventHandlers) _allNavigatorEventHandlers[i](event);
}

function restartApp(login, stopsBookmarks) {
	EventEmitter.removeListener('XMPP_MessageReceived');
	let storeStopsBookmarks = [{
		description: "home",
		name: null
	}, {
		description: "office",
		name: null
	}];

	if (stopsBookmarks) {
		let home = stopsBookmarks.nodes.find(({description}) => description == 'home');
		if (home) {
			storeStopsBookmarks[0] = home
		}
		let office = stopsBookmarks.nodes.find(({description}) => description == 'office');
		if (office) {
			storeStopsBookmarks[1] = office
		}
	}

	setItem('stopsBookmarks', storeStopsBookmarks);

	if (isIOS) {
		startApp(login, 'Penumpang', 2)
	} else {
		setItem('login', login);
		setItem('mode', 'Penumpang');
		broadcast({
			action: 'onLogin',
			payload: {
				login,
				stopsBookmarks: storeStopsBookmarks
			}
		});
		showLoading(false)
	}
}

export {
	restartApp,
	getRegisteredScreen,
	startApp,
	setEventHandler,
	clearEventHandler,
	broadcast
};
