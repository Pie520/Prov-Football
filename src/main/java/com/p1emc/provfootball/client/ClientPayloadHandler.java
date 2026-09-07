package com.p1emc.provfootball.client;

import com.p1emc.provfootball.network.ChargeCancelPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// ModNetworking loads on BOTH sides, so it must never reference ChargeTracker
// directly -- that class is Dist.CLIENT and a dedicated server would crash
// resolving it. Routing through this class keeps the client-only reference
// inside a file the server never touches.
public class ClientPayloadHandler {

    public static void handleCancel(ChargeCancelPayload payload, IPayloadContext context) {
        context.enqueueWork(ChargeTracker::cancel);
    }
}