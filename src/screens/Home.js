/* @flow */
import React, {PureComponent} from 'react';
import {
	Keyboard,
	FlatList,
	Image,
	StyleSheet,
	Text,
	TextInput,
	TouchableHighlight,
	View,
	ActivityIndicator
} from 'react-native';
import keyExtractor from 'keyextractor';
import {isIOS} from 'constants';
import {graphql} from "../libs/api";
import navigationStyle from "../libs/navigationStyle";

export default class Home extends PureComponent<Props, State> {
	constructor(props){
		super(props);
		this.state = {
			nodes: [],
			userCount: 0
		};
		this.endCursor = null;
		this.query = "";
		console.log("dsds:");
	};

	handleSearchChange = async ({nativeEvent: {text}}) => {
		this.query = text.toLowerCase();
		if (this.query) {
			this.fetchData()
		} else {
			this.setState({
				userCount: 0,
				nodes: []
			});
			this.endCursor = null;
		}
	};

	renderListItem = ({item:{login,name,avatarUrl}}) => (
		<TouchableHighlight
			underlayColor="rgba(0, 0, 0, 0.05)"
			onPress={()=> {
				Keyboard.dismiss();
				this.props.navigation.push({
					screen:"Profile",
					animationType: "slide-horizontal",
					title: "Detail Github User",
					navigationStyle: {
						...navigationStyle,
						tabBarHidden: true
					},
					passProps: {
						uri: `https://github.com/${login}`,
						isShowLoading: true
					}
				})
			}}>
			<View style={styles.row}>
				<Image style={styles.avatar} source={{uri:avatarUrl}} />
				<Text style={styles.text}>{`${name || login}\n${login}`}</Text>
				<View style={styles.cellAccessoryView}>
					<View style={styles.accessory_disclosureIndicator} />
				</View>
			</View>
		</TouchableHighlight>
	);

	render() {
		const {nodes, userCount} = this.state;
		return (
			<View style={styles.container}>
				<View style={styles.searchBar}>
					<TextInput
						autoCapitalize="none"
						underlineColorAndroid="transparent"
						autoCorrect={false}
						autoFocus
						onChange={this.handleSearchChange}
						placeholder={"Search by username"}
						style={styles.searchBarInput}
					/>
				</View>
				<FlatList
					data={nodes}
					style={styles.list}
					onEndReached={this._onEndReached}
					ListFooterComponent={nodes.length && (nodes.length < userCount) ? (
						<View style={styles.row}>
							<ActivityIndicator />
							<Text style={{marginLeft:10}} children={"Load More..."}/>
						</View>
					) : null}
					renderItem={this.renderListItem}
					automaticallyAdjustContentInsets={false}
					keyboardDismissMode="on-drag"
					keyboardShouldPersistTaps="always"
					showsVerticalScrollIndicator={false}
					initialNumToRender={20}
					keyExtractor={keyExtractor}
				/>
			</View>
		);
	}

	async fetchData(isMergeData: boolean) {
		const {search: {userCount, pageInfo, nodes}} = await graphql({
			variables: {
				query: this.query,
				after: this.endCursor
			},
			log: true
		});
		if (isMergeData) {
			this.setState({
				userCount,
				nodes: this.state.nodes.concat(nodes)
			});
		} else {
			this.setState({userCount, nodes});
		}

		this.endCursor = pageInfo.endCursor;
	}

	_onEndReached = () => {
		const {nodes, userCount} = this.state;
		if (nodes.length && (nodes.length < userCount)) {
			this.fetchData(true);
		}
	}
}

const imageSize = 35;

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: 'white',
	},
	avatar: {
		borderRadius: imageSize * 0.5,
		borderWidth: 1,
		borderColor: 'transparent',
		justifyContent: 'center',
		alignItems: 'center',
		width: imageSize,
		height: imageSize,
		marginRight: 15
	},
	searchBar: {
		marginTop: 0,
		padding: 3,
		paddingLeft: 8,
		flexDirection: 'row',
		alignItems: 'center',
		borderBottomWidth: StyleSheet.hairlineWidth,
		borderColor: '#ccc',
	},
	searchBarInput: {
		fontSize: 15,
		flex: 1,
		height: !isIOS ? 45 : 30,
	},
	list: {
		flex: 1,
	},
	row: {
		flexDirection: 'row',
		justifyContent: 'center',
		padding: 10,
		overflow: 'hidden',
		backgroundColor: "rgb(194,207,213)",
		borderBottomWidth: 1,
		borderColor: 'white',
	},
	icon: {
		textAlign: 'center',
		marginRight: 20,
		width: 20,
	},
	text: {
		flex: 1
	},
	cellAccessoryView: {
		justifyContent: 'center',
		marginRight: 15
	},
	accessory_disclosureIndicator: {
		width: 10,
		height: 10,
		marginLeft: 7,
		backgroundColor: 'transparent',
		borderTopWidth: 1,
		borderRightWidth: 1,
		borderColor: 'black',
		transform: [
			{
				rotate: '45deg',
			},
		],
	}
});