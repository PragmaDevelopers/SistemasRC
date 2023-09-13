import { CalendarIcon, ChartPieIcon, CogIcon, ServerStackIcon, UserGroupIcon } from "@heroicons/react/24/solid";
import Image from "next/image";
import Link from "next/link";

interface BoardMenuEntryProps {
    href: string;
    name: string;
    picture: string;
}

function BoardMenuEntry(props: BoardMenuEntryProps) {
    return (
        <Link href={props.href}>
            <div>
                <Image src={props.picture} alt={props.name} width={640} height={640} />
            </div>
            <h1>{props.name}</h1>
        </Link>
    );
}

export default function Page() {
    const IconStyles: string = "w-8 aspect-square";

    return (
        <main>
            <div>
                <form>
                    <input type="text" placeholder="Pesquisar Card" />
                </form>
                <details>
                    <summary></summary>
                    <div>
                        <Link href="">
                            <UserGroupIcon className={IconStyles} />
                            <h1>Usuários</h1>
                        </Link>
                        <Link href="">
                            <ServerStackIcon className={IconStyles} />
                            <h1>Areas de Trabalho</h1>
                        </Link>
                        <Link href="">
                            <ChartPieIcon className={IconStyles} />
                            <h1>Relatorios</h1>
                        </Link>
                        <Link href="">
                            <CalendarIcon className={IconStyles} />
                            <h1>Caléndario</h1>
                        </Link>
                    </div>
                </details>
                <details>
                    <summary></summary>
                    <div>
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="https://cdn.ebaumsworld.com/mediaFiles/picture/1151541/84693449.png" name="nome" />
                    </div>
                </details>
                <div>
                    <CogIcon className={IconStyles} />
                    <h1>Configurações</h1>
                </div>
            </div>
            <div>
            </div>
        </main>
    );
}
