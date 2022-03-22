class MessageHelper {
  messageId = 0;

  constructor() {}

  generateId() {
    const lastMessageId = this.messageId;
    this.messageId++;
    return lastMessageId;
  }

  incrementMessageId() {
    this.messageId++;
  }

  getActualMessageId() {
    return this.messageId;
  }
}

export default new MessageHelper();
