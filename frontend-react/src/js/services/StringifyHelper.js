class StringifyHelper {
  constructor() {}

  stringify(messageType, message) {
    return JSON.stringify({
      messageType: messageType,
      message: message,
    });
  }
}

export default new StringifyHelper();
