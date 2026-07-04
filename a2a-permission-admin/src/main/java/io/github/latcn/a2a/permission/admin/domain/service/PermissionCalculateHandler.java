package io.github.latcn.a2a.permission.admin.domain.service;

public abstract class PermissionCalculateHandler {

    protected PermissionCalculateHandler next;

    public void setNext(PermissionCalculateHandler next) {
        this.next = next;
    }

    public abstract void handle(PermissionContext context);

    public void execute(PermissionContext context) {
        handle(context);
        if (next != null && !context.hasError()) {
            next.execute(context);
        }
    }
}