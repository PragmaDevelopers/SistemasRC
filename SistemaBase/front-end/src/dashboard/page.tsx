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
        <Link href={props.href} className="my-2">
            <Image src={props.picture} alt={props.name} width={640} height={640} className="w-8 aspect-square rounded-md overflow-hidden" />
            <h1>{props.name}</h1>
        </Link>
    );
}

export default function Page() {
    const IconStyles: string = "w-8 aspect-square mr-2";

    return (
        <main className="w-full h-full overflow-x-hidden flex-col items-start justify-start">
            <div className="relative w-48 h-full flex flex-col justify-start items-start">
                <form>
                    <input type="text" placeholder="Pesquisar Card" />
                </form>
                <details className="p-2">
                    <summary>Seções</summary>
                    <div className="overflow-x-hidden overflow-y-auto">
                        <Link href="/" className="flex flex-row justify-between items-center">
                            <UserGroupIcon className={IconStyles} />
                            <h1>Usuários</h1>
                        </Link>
                        <Link href="/" className="flex flex-row justify-between items-center">
                            <ServerStackIcon className={IconStyles} />
                            <h1>Areas de Trabalho</h1>
                        </Link>
                        <Link href="/" className="flex flex-row justify-between items-center">
                            <ChartPieIcon className={IconStyles} />
                            <h1>Relatorios</h1>
                        </Link>
                        <Link href="/" className="flex flex-row justify-between items-center">
                            <CalendarIcon className={IconStyles} />
                            <h1>Caléndario</h1>
                        </Link>
                    </div>
                </details>
                <details className="p-2">
                    <summary>Areas de Trabalho</summary>
                    <div className="overflow-x-hidden overflow-y-auto">
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                        <BoardMenuEntry href="/" picture="/84693449.png" name="nome" />
                    </div>
                </details>
                <Link href="/" className="absolute bottom-0 bg-neutral-50 p-2 flex flex-row justify-between items-center">
                    <CogIcon className={IconStyles} />
                    <h1>Configurações</h1>
                </Link>
            </div>
            <div className="grow">
            </div>
        </main>
    );
}
