console.disableYellowBox = true;

if (!__DEV__) {
	console = {};
	console.log = () => {};
	console.error = () => {};
}

import {startApp} from './src/modules/Navigation';
startApp();