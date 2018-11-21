import {DeviceEventEmitter, NativeAppEventEmitter, Platform} from 'react-native';

export default class NativeEventsReceiver {
  constructor() {
    this.emitter = Platform.OS === 'android' ? DeviceEventEmitter : NativeAppEventEmitter;
  }

  appLaunched(callback) {
    this.emitter.addListener('TEBAppLaunched', callback);
  }
}
