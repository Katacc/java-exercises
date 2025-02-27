# Today week 06 v1

Changes in this version are detailed below by class or interface.

## EventProvider

New method: `String getIdentifier()`

Classes that implement the `EventProvider` interface should
maintain a unique private identifier string and return it
when `getIdentifier` is called.

## EventManager

New class using the Singleton design pattern.
Only one instance of this class can ever exist.
This is ensured with a private constructor, and
a private member `INSTANCE` (which is the only instance).

Clients can get a reference to this class with:

    EventManager.getInstance()

All calls to this method return a reference to the same
private member `INSTANCE`.

### Methods

The `EventManager` singleton class has new methods:

    boolean addEventProvider(EventProvider provider)
    boolean removeEventProvider(String providerId)

See the JavaDoc comments for details.

The `EventManager` singleton keeps a list of event providers.
They can be added or removed. In this version, removing
event providers is not supported, so the method throws
`UnsupportedOperationException`. You are not supposed to catch
this exception, but instead implement the method.

When adding event providers, the implementation should check
that the provider is not already registered, based on the identifier.

    int getEventProviderCount()
    List<String> getEventProviderIdentifiers()

These methods get the count of currently known event providers,
and get their identifiers. This is useful for removing event
providers.

## Today test class

The main class has some test code that adds event providers
and then gets their events. It also tries to get the provider
identifiers and to remove one of them, but these operations will
fail because they haven't been implemented yet.

## FirstEventProvider and SecondEventProvider

These are just demonstration classes that implement the 
`EventProvider` interfaces, so that they can be used to
test the `EventManager` operations. They each provide one
event which can be used to identify the provider it came
from. Actually, the `SecondEventProvider` class is a subclass
of `FirstEventProvider`, and it initializes its identifier
by calling the superclass constructor with `super`.

## CSVEventProvider

Updated to implement the new `EventProvider` interface.
Events are loaded using the OpenCSV library.
