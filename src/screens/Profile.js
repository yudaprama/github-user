import React from 'react';
import {WebView} from "react-native";
import showLoading from "../libs/showLoading";

export default ({uri="https://www.linkedin.com/in/yuda-prama-4b32aa137?originalSubdomain=id", isShowLoading=false}) => (
	<WebView
		onLoadStart={() => isShowLoading ? showLoading(true, 'Loading') : null}
		onLoad={() => isShowLoading ? showLoading(false) : null}
		onLoadEnd={() => isShowLoading ? showLoading(false) : null}
		source={{uri}} />
);