'use client';

import { usePathname } from "next/navigation";
import { CalendarIcon, ChartPieIcon, CogIcon, ServerStackIcon, UserGroupIcon } from "@heroicons/react/24/solid";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";
import { BuildingOffice2Icon } from "@heroicons/react/24/solid";
import { throws } from "assert";

interface BoardMenuEntryProps {
    href: string;
    name: string;
    //picture: string;
}

//function BoardMenuEntry(props: BoardMenuEntryProps) {
//    return (
//        <Link href={props.href} className="my-2 flex flex-row items-center">
//            <Image src={props.picture} alt={props.name} width={640} height={640} className="w-8 aspect-square rounded-md overflow-hidden mr-2" />
//            <h1>{props.name}</h1>
//        </Link>
//    );
//}

function BoardMenuEntry(props: BoardMenuEntryProps) {
    return (
        <Link href={props.href} className="my-2 flex flex-row items-center">
            <BuildingOffice2Icon className="w-6 aspect-square mr-2" />
            <h1>{props.name}</h1>
        </Link>
    );
}

export default function Layout({ children }: any) {
    const [dashboards, setDashboards] = useState<{ kanbanId: string, name: string }[]>();
    const IconStyles: string = "w-8 aspect-square mr-2";

    useEffect(() => {
        fetch("http://localhost:8080/api/dashboard/kanban/getall").then(response => response.json()).then(data => setDashboards(data))
    }, [setDashboards]);

    const addDashBoard = (event: any) => {
        event.preventDefault();
        const dashboardItem: { name: string, kanbanId: string } = {
            name: event.target.boardname.value, kanbanId: generateRandomString()
        }

        if (dashboards !== undefined) {
            if (dashboards?.length >= 0) {
                setDashboards([...dashboards as unknown as { name: string, kanbanId: string }[], dashboardItem]);
            } else {
                setDashboards([dashboardItem]);
            }
        } else {
            setDashboards([dashboardItem]);
        }

        console.log(dashboards);

        if (dashboardItem !== undefined) {
            console.log(JSON.stringify(dashboardItem));
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    kanbanId: dashboardItem.kanbanId,
                    name: dashboardItem.name,
                }),
            };
            fetch('http://localhost:8080/api/dashboard/kanban/create', requestOptions).then(response => response.json()).then(data => console.log(data));
        }
    }

    return (
        <main className="w-full h-full flex flex-row items-start justify-between">
            <div className="grow relative w-56 h-full flex flex-col justify-start items-start shrink-0">
                <form className="hidden">
                    <input type="text" placeholder="Pesquisar Card" />
                </form>
                <details className="p-2">
                    <summary>Seções</summary>
                    <div className="">
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <UserGroupIcon className={IconStyles} />
                            <h1>Usuários</h1>
                        </Link>
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <ServerStackIcon className={IconStyles} />
                            <h1>Areas de Trabalho</h1>
                        </Link>
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <ChartPieIcon className={IconStyles} />
                            <h1>Relatorios</h1>
                        </Link>
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <CalendarIcon className={IconStyles} />
                            <h1>Caléndario</h1>
                        </Link>
                    </div>
                </details>
                <div className="h-full flex flex-col justify-between overflow-hidden">
                    <details className="p-2 overflow-x-hidden overflow-y-auto">
                        <summary>Areas de Trabalho</summary>
                        <div className="">
                            {dashboards?.map((element) => <BoardMenuEntry href={`/dashboard/board/${element.kanbanId}`} name={element.name} />)}
                        </div>
                        <div>

                            <form onSubmit={addDashBoard}>
                                <input className="w-full" name="boardname" placeholder="Board Name" type="text" />
                                <button type="submit">Create New Board</button>
                            </form>
                        </div>
                    </details>
                    <Link href="/" className="bg-neutral-50 p-2 flex flex-row items-center">
                        <CogIcon className={IconStyles} />
                        <h1>Configurações</h1>
                    </Link>
                </div>
            </div>
            <div className="grow w-full h-full p-2 bg-neutral-100 rounded-tl-md">
                {children}
            </div>
        </main>
    );
}

function generateRandomString(): string {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const format = [10, 21];

    for (let i = 0; i < 32; i++) {
        // Insert hyphens at the specified positions
        if (format.includes(i)) {
            result += '-';
        } else {
            const randomIndex = Math.floor(Math.random() * characters.length);
            result += characters[randomIndex];
        }
    }

    return result;
}

