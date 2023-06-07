import React, {createContext, useContext, useState} from "react";
import {Button, Modal} from "react-bootstrap";

interface AlertContext {
    isShown(): boolean
    hide(): void
    getBuilder(): AlertBuilder
}

interface AlertBuilder {
    setTitle(title: JSX.Element | string): AlertBuilder
    setMessage(message: JSX.Element | string): AlertBuilder
    setButtonsOk(okCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder
    setButtonsOkCancel(okCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, cancelCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder
    setButtonsYesNoCancel(yesCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, noCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, cancelCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder
    setButtonsYesNo(yesCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, noCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder
    addButton(button: JSX.Element): AlertBuilder
    show(): void
}

const AlertContext = createContext<AlertContext | null>(null);

function AlertContextProvider({ children }: {
    children: [JSX.Element]
}) {
    const [ show, setShow ] = useState(false);
    const [ title, setTitle ] = useState(<></>);
    const [ message, setMessage ] = useState(<></>);
    const [ buttons, setButtons ] = useState<JSX.Element[]>([]);

    const buttonCallback = (callback: React.MouseEventHandler<HTMLButtonElement> | undefined) => {
        return (event: React.MouseEvent<HTMLButtonElement>) => {
            if (typeof callback === "function")
                callback(event);
            setShow(false);
        };
    };

    const value: AlertContext = {
        isShown(): boolean {
            return show
        },

        hide(): void {
            setShow(false)
        },

        getBuilder(): AlertBuilder {
            return new class implements AlertBuilder {
                addButton(button: JSX.Element): AlertBuilder {
                    setButtons([...buttons, button])
                    return this
                }

                setButtonsOk(okCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder {
                    setButtons([
                        <Button variant={"primary"} onClick={buttonCallback(okCallback)}>Ok</Button>
                    ])
                    return this
                }

                setButtonsOkCancel(okCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, cancelCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder {
                    setButtons([
                        <Button variant={"primary"} onClick={buttonCallback(okCallback)}>Ok</Button>,
                        <Button variant={"danger"} onClick={buttonCallback(cancelCallback)}>Cancel</Button>
                    ])
                    return this
                }

                setButtonsYesNo(yesCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, noCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder {
                    setButtons([
                        <Button variant={"primary"} onClick={buttonCallback(yesCallback)}>Yes</Button>,
                        <Button variant={"secondary"} onClick={buttonCallback(noCallback)}>No</Button>
                    ])
                    return this
                }

                setButtonsYesNoCancel(yesCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, noCallback: React.MouseEventHandler<HTMLButtonElement> | undefined, cancelCallback: React.MouseEventHandler<HTMLButtonElement> | undefined): AlertBuilder {
                    setButtons([
                        <Button variant={"primary"} onClick={buttonCallback(yesCallback)}>Yes</Button>,
                        <Button variant={"secondary"} onClick={buttonCallback(noCallback)}>No</Button>,
                        <Button variant={"danger"} onClick={buttonCallback(cancelCallback)}>Cancel</Button>
                    ])
                    return this
                }

                setMessage(message: JSX.Element | string): AlertBuilder {
                    setMessage(<>{message}</>)
                    return this
                }

                setTitle(title: JSX.Element | string): AlertBuilder {
                    setTitle(<>{title}</>)
                    return this
                }

                show(): void {
                    setShow(true)
                }
            }
        }
    }

    return (
        <AlertContext.Provider value={value}>
            {children}
            <Modal show={show} onHide={() => setShow(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{title}</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {message}
                </Modal.Body>

                <Modal.Footer>
                    {buttons}
                </Modal.Footer>
            </Modal>
        </AlertContext.Provider>
    );
}

function useAlert(): AlertContext {
    const c = useContext(AlertContext)
    if (!c) {
        throw new Error("useAlert has to be used inside <AlertContextProvider>")
    }
    return c
}

export {
    AlertContextProvider,
    useAlert
};