<h1>Endpoints</h1>

<h2>GET</h2>

<ul>
    <li>
        <a>/api/private/user/kanban</a>
        <br />
        <i>Precisa do token</i>
        <p></p>
        <p>Retorna todos os kanban do usuário logado</p>
        <code>
            [
                {
                    "id": 2,
                    "title": "Criar marca 2"
                },
                {
                    "id": 4,
                    "title": "Criar marca 3"
                }
            ]
        </code>
        <p></p>
    </li>
    <li>
        <a>/api/private/user/kanban/{kanban_id}/columns?cards=true</a>
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
            &nbsp;&nbsp;&nbsp;&nbsp;"members": "lucas@emainsad.com,nicolassa@gmail.com",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"description": "desc card"<br />
            &nbsp;&nbsp;&nbsp;},<br />
            &nbsp;&nbsp;&nbsp;{<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"id": 2,<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"title": "Nome do card",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"tags": "ola,sad,sadsa",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"members": "lucas@emainsad.com,nicolassa@gmail.com",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"description": "desc card"<br />
            &nbsp;&nbsp;&nbsp;&nbsp;},<br />
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
            &nbsp;&nbsp;&nbsp;&nbsp;"members": "lucas@emainsad.com,nicolassa@gmail.com",<br />
            &nbsp;&nbsp;&nbsp;&nbsp;"description": "desc card"<br />
            &nbsp;&nbsp;&nbsp;}<br />
            &nbsp;&nbsp;]<br />
            &nbsp;}<br />
            ]
        </code>
    </li>
</ul>
