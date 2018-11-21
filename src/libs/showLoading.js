import {ProgressHUB} from 'NativeModules';

export default (isShow: boolean, title: string = 'Memproses...') => {
	if (isShow) {
		ProgressHUB.showSpinIndeterminateWithTitle(title)
	} else {
		ProgressHUB.dismiss()
	}
}