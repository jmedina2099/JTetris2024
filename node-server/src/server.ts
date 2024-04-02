import { createServer } from "http";
import { Server, Socket } from "socket.io";
import { Kafka, KafkaConfig } from 'kafkajs';
import { Consumer, EachMessagePayload } from 'kafkajs';

const kafkaConfig: KafkaConfig = { brokers: ['localhost:9092'] };
const kafka = new Kafka(kafkaConfig);

const consumer = kafka.consumer({ groupId: 'figureMessageNode' })

let socketConnected : Socket;

await consumer.connect()
await consumer.subscribe({ topics: ['nextFigureTopic','figureTopic','boardTopic'], fromBeginning: false })
await consumer.run({
  eachMessage: async ({ topic, partition, message }: EachMessagePayload) => {
    //console.log( 'topic ===> '+topic )
    if( message.value !== null ) {
      let json = message.value.toString();
      //console.log(JSON.stringify(JSON.parse(json),null,2));
      //console.log({value: json});
      if( socketConnected ) {
        if( topic == 'nextFigureTopic' ) {
          socketConnected.emit( 'nextFigureMessage', json );
        } else if( topic == 'figureTopic' ) {
          socketConnected.emit( 'fallingFigureMessage', json );
        } else if( topic == 'boardTopic' ) {
          socketConnected.emit( 'boardMessage', json );
        }
      }
    }
  },
})

const httpServer = createServer();
const io = new Server(httpServer, {
    cors: {
        origin: [ "http://localhost:8080", "http://localhost:9081", "http://localhost:4200" ],
        methods: ["GET", "POST"]
    }
});

io.on("connection", (socket: Socket) => {
    console.log('on connection !!!!');
    socketConnected = socket;
});

httpServer.listen(4444, () => {
    console.log('listening on *:4444');
});
