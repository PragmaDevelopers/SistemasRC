const { connect } = require('imap');
const { simpleParser } = require('mailparser');
const WebSocket = require('ws');

const imapConfig = {
  user: 'seu_email@gmail.com',
  password: 'sua_senha',
  host: 'imap.gmail.com',
  port: 993,
  tls: true,
};

const listaTRE = ['tre1@example.com', 'tre2@example.com']; ///procurar os emails do tre dps

const wss = new WebSocket.Server({ port: 8080 }); //padrao q peguei pesquisar dps

const imap = connect(imapConfig);

imap.on('ready', () => {
  imap.openBox('INBOX', true, (err) => {
    if (err) throw err;

    imap.on('mail', (numNewMsgs) => {
      console.log(`Novas mensagens: ${numNewMsgs}`);

      imap.search(['UNSEEN'], (searchErr, results) => {
        if (searchErr) throw searchErr;

        results.forEach((messageNumber) => {
          const fetch = imap.fetch([messageNumber], { bodies: '' });

          fetch.on('message', (msg) => {
            msg.on('body', (stream) => {
              simpleParser(stream, async (parseErr, parsed) => {
                if (parseErr) throw parseErr;

                const fromEmail = parsed.from.value[0].address;

                if (listaTRE.includes(fromEmail)) {
                  wss.clients.forEach((client) => {
                    if (client.readyState === WebSocket.OPEN) {
                      client.send(`Nova mensagem de ${fromEmail}: ${parsed.subject}`);
                    }
                  });
                }
              });
            });
          });
        });
      });
    });
  });
});

imap.on('error', (err) => {
  console.error(err);
});

imap.connect();
