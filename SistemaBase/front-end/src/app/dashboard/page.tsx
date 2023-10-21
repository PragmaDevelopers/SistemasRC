import { PlusCircleIcon } from "@heroicons/react/24/outline";

export default function Page() {
    return (
        <main className="w-full h-full overflow-x-hidden flex flex-col items-center justify-center">
            <div className="select-none flex justify-start items-start flex-col w-max">
                <h1 className="text-neutral-400 text-xl font-semibold my-1">Para começar, crie uma nova Área de Trabalho no menu lateral direito.</h1>
                <h2 className="text-neutral-400 my-1 flex items-center justify-centers">Basta adicionar um nome no campo <span className="text-neutral-400 border-2 border-neutral-400 rounded-md p-0.5 mx-1">Adicionar nova area</span> e por fim clicar no botão <span><PlusCircleIcon className="stroke-neutral-400 ml-1 aspect-square w-8" /></span></h2>
            </div>
        </main>
    );
}
