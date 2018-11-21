// 5af3dd666dabadbb77f797d3262cd01b9c1c2dd6

import {showSnackbar} from "../modules/Navigation/platformSpecific";

export const queryUserSearch = `
query(
  $query: String!
  $after: String
) { 
  search(
    type: USER
    query: $query
    first: 10
    after: $after
  ) {
    userCount
    pageInfo {
      endCursor
      startCursor
    }
    nodes {
      ... on User {
        login
        avatarUrl
        name
      }
    }
  }
}`;

export const graphql = ({variables, log}) => {
	if (log) {
		console.log("variables:", variables);
	}
	return new Promise((resolve, reject) => {
		fetch('https://api.github.com/graphql', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': 'bearer 5af3dd666dabadbb77f797d3262cd01b9c1c2dd6'
			},
			body: JSON.stringify({
				query: queryUserSearch,
				variables
			})
		})
		.then(response => response.json())
		.then(({data, errors}) => {
			if (log) {
				console.log({data, errors})
			}
			if (errors) {
				const error = errors.map(({message}) => message).join("; ");
				// recordError(error);
				reject(error)
			} else {
				resolve(data)
			}
		})
		.catch((e) => {
			showSnackbar("Network request failed");
			reject(e)
		})
	})
};