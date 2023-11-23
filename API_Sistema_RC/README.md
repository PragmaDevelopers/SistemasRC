<h2>Error Status</h2>
<ul>
    <li>
        <b>430 - (CARD)</b>
        <p>Está faltando alguma propriedade no objeto do card enviado!</p>
    </li>
    <li>
        <b>431 - (CARD)</b>
        <p>Tentar ver, mover, deletar, criar ou atualizar sem estar no kanban!</p>
    </li>
    <li>
        <b>432 - (CARD)</b>
        <p>Tentar mover um card de um kanban para o outro de forma incorreta!</p>
    </li>
    <li>
        <b>433 - (CARD)</b>
        <p>Tentar adicionar um membro ao card que não esteja convidado no kanban!</p>
    </li>
    <li>
        <b>434 - (CARD)</b>
        <p>O card não foi encontrado!</p>
    </li>
    <li>
        <b>435 - (CARD)</b>
        <p>Tentar ver, mover, deletar, criar ou atualizar sem ter a autorização necessária!</p>
    </li>
    <li>
        <b>424 - (COLUMN)</b>
        <p>A coluna não foi encontrado!</p>
    </li>
    <li>
        <b>411 - (KANBAN)</b>
        <p>Tentar deletar e atualizar sem autorização!</p>
    </li>
    <li>
        <b>416 - (KANBAN)</b>
        <p>Convidar um usuario já existente no kanban!</p>
    </li>
</ul>

<h2>Endpoints</h2>

<h3>Kanban</h3>

<ul>
    <li>
        <h4>GET - <a>/api/private/user/kanban</a></h4>
        <i>Precisa do token</i>
        <p></p>
        <p>Retorna todos os kanban do usuário logado:</p>
        <b>Response - OK</b>
        <p></p>
        <code>
            [<br />
            &nbsp;{<br />
            &nbsp;&nbsp;"id": 2,<br />
            &nbsp;&nbsp;"title": "Criar marca 2"<br />
            &nbsp;},<br />
            &nbsp;{<br />
            &nbsp;&nbsp;"id": 4,<br />
            &nbsp;&nbsp;"title": "Sistema RC"<br />
            &nbsp;}<br />
            ]
        </code>
        <p></p>
    </li>
    <li>
        <h4>GET - <a>/api/private/user/kanban/{kanbanId}</a></h4>
        <i>Precisa do token</i>
        <p></p>
        <p>Retorna todos os usuários cadastrados no kanban selecionado:</p>
        <b>Response - OK</b>
        <p></p>
        <code>
            [<br />
            &nbsp;{<br />
            &nbsp;&nbsp;"id": 2,<br />
            &nbsp;&nbsp;"name": "Lucas"<br />
            &nbsp;&nbsp;"email": "lucas@gmail.com"<br />
            &nbsp;&nbsp;"pushEmail": null<br />
            &nbsp;&nbsp;"registration_date": "2023-11-22"<br />
            &nbsp;&nbsp;"nationality": "brasileiro"<br />
            &nbsp;&nbsp;"gender": "masculino"<br />
            &nbsp;&nbsp;"role": "ROLE_PROFESSIONAL"<br />
            &nbsp;&nbsp;"kanban_role": "ADMIN"<br />
            &nbsp;&nbsp;"kanban_role": "ADMIN"<br />
            &nbsp;&nbsp;"kanban_role": "ADMIN"<br />
            &nbsp;},<br />
            &nbsp;{<br />
            &nbsp;&nbsp;"id": 4,<br />
            &nbsp;&nbsp;"title": "Sistema RC"<br />
            &nbsp;}<br />
            ]
        </code>
        <p></p>
    </li>
    <li>
        <h4>POST - <a>/api/private/user/kanban</a></h4>
        <i>Precisa do token</i>
        <p></p>
        <p>Cria um kanban:</p>
        <b>Body</b>
        <p></p>
        <code>{"title": "Sistema RC"}</code>
        <p></p>
        <p>Retorna o id do kanban criado:</p>
        <b>Response - OK</b>
        <p></p>
        <code>4</code>
        <p></p>
        <p>Exemplo de campo title inexistente</p>
        <b>Response - Error</b>
        <p></p>
        <code>{"mensagem": "O campo kanbanId é necessário!","status": 410}</code>
        <p></p>
    </li>
    <li>
        <h4>PATCH - <a>/api/private/user/kanban/{kanbanId}</a></h4>
        <i>Precisa do token</i>
        <p></p>
        <p>Atualiza um kanban:</p>
        <b>Body</b>
        <p></p>
        <code>{"title": "Sistema RC beta"}</code>
        <p></p>
        <p>Exemplo de kanbanId inexistente:</p>
        <b>Response - Error</b>
        <p></p>
        <code>{"mensagem": "Kanban não foi encontrado!"}</code>
        <p></p>
    </li>
    <li>
        <h4>DELETE - <a>/api/private/user/kanban/{kanbanId}</a></h4>
        <i>Precisa do token</i>
        <p>Exemplo de kanbanId inexistente:</p>
        <b>Response - Error</b>
        <p></p>
        <code>{"mensagem": "Kanban não foi encontrado!"}</code>
    </li>
</ul>

<h3>Column</h3>

<ul>
    <li>
        <a>/api/private/user/kanban/{kanbanId}/columns?cards=true</a>
        <br />
        <i>Precisa do token</i>
        <p></p>
        <p>Retorna todas as colunas do usuário logado. OBS: parametro opcional cards (boolean) para trazer o campo cards para cada coluna</p>
        <code>
            [<br />
            &nbsp;{<br />
            &nbsp;&nbsp;"id": 1,<br />
            &nbsp;&nbsp;"title": "Ola",<br />
            &nbsp;&nbsp;"cards": [<br />
            &nbsp;&nbsp;&nbsp;{<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"id": 1,<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"title": "Nome do card",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"tags": "ola,sad,sadsa",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"members": "1,3",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"description": "desc card"<br />
            &nbsp;&nbsp;&nbsp;}<br />
            &nbsp;&nbsp;]<br />
            &nbsp;},<br />
            &nbsp;{<br />
            &nbsp;&nbsp;"id": 2,<br />
            &nbsp;&nbsp;"title": "Tchau",<br />
            &nbsp;&nbsp;"cards": [<br />
            &nbsp;&nbsp;&nbsp;{<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"id": 5,<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"title": "Nome do card",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"tags": "ola,sad,sadsa",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"members": "2,3",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"description": "desc card"<br />
            &nbsp;&nbsp;&nbsp;}<br />
            &nbsp;&nbsp;]<br />
            &nbsp;}<br />
            ]
        </code>
    </li>
</ul>
